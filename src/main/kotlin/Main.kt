import command.CommandOutput
import command.handler.CommandHandlerMapper
import org.slf4j.LoggerFactory
import persistence.aof.Aof
import resp.RespReader
import resp.RespWriter
import resp.Value
import java.net.ServerSocket
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val server = MiniRedisServer()
    server.initialize()

    server.use {
        it.start()
    }
}

class MiniRedisServer : AutoCloseable {
    private val server: ServerSocket
    private val handlerMapper: CommandHandlerMapper
    private val aof: Aof

    init {
        println("Listening on port :6379")
        server = ServerSocket(6379)
        handlerMapper = CommandHandlerMapper()
        aof = Aof.create(fileName = "database.aof")
    }

    fun start() {
        val client = server.accept()
        client.use {
            val resp = RespReader.of(it.getInputStream())
            val writer = RespWriter.of(it.getOutputStream())
            while (true) {
                val readValue = try {
                    resp.readValue()
                } catch (e: Exception) {
                    println("error reading from client: ${e.message}")
                    exitProcess(1)
                }

                val commandOutput = handle(readValue)
                writer.write(commandOutput.output)
            }
        }
    }

    private fun handle(readValue: Value): CommandOutput {
        val command = readValue.array.first().str.uppercase()
        val args = readValue.array.drop(1)
        println("Read =====  $readValue")

        val handler = handlerMapper.getHandler(command)
        return handler.invoke(args)
    }

    fun initialize() {
        aof.read { value ->
            handle(value)
        }
    }

    override fun close() {
        server.close()
        aof.close()
    }

    companion object {
        private val log = LoggerFactory.getLogger(MiniRedisServer::class.java)
    }
}