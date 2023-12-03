import resp.RespReader
import resp.RespWriter
import resp.Value
import resp.ValueType
import java.net.ServerSocket
import java.net.Socket
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val server = MiniRedisServer()
    server.start()
}

class MiniRedisServer : AutoCloseable {

    private val server: ServerSocket
    private lateinit var conn: Socket

    init {
        println("Listening on port :6379")
        server = ServerSocket(6379)
    }

    fun start() {

        val client = server.accept()

        client.use {

            val resp = RespReader.of(it.getInputStream())
            val writer = RespWriter.of(it.getOutputStream())

            while (true) {
                try {
                    val r = resp.read()
                    println(r)
                } catch (e: Exception) {
                    println("error reading from client: ${e.message}")
                    exitProcess(1)
                }
                writer.write(Value(type = ValueType.STRING, str = "OK"))
            }

        }
    }


    fun handleClient(conn: Socket) {

    }

    override fun close() {
        server.close()
    }
}