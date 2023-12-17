import command.handler.CommandHandlerMapper
import org.slf4j.LoggerFactory
import resp.RespReader
import resp.RespWriter
import java.net.ServerSocket
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val server = MiniRedisServer()
    server.start()
}

class MiniRedisServer : AutoCloseable {
    private val server: ServerSocket
    private val handlerMapper: CommandHandlerMapper

    init {
        println("Listening on port :6379")
        server = ServerSocket(6379)
        handlerMapper = CommandHandlerMapper()
    }

    fun start() {
        val client = server.accept()
        client.use {
            val resp = RespReader.of(it.getInputStream())
            val writer = RespWriter.of(it.getOutputStream())

            while (true) {
                val readValue = try {
                    resp.read()
                } catch (e: Exception) {
                    println("error reading from client: ${e.message}")
                    exitProcess(1)
                }
                val command = readValue.array.first().str.uppercase()
                val args = readValue.array.drop(1)
                println("Read =====  $readValue")

                val handler = handlerMapper.getHandler(command)
                writer.write(handler.invoke(args))
            }
        }
    }

    override fun close() {
        server.close()
    }

    companion object {
        private val log = LoggerFactory.getLogger(MiniRedisServer::class.java)
    }
}