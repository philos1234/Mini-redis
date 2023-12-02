import resp.RespReader
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
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
//        val bufferedInputStream = BufferedInputStream(client.getInputStream())

        client.use {

            val resp = RespReader(it.getInputStream())
            val writer = PrintWriter(it.getOutputStream(), true)

            while (true) {
                try {
                    val readCount = resp.read()

                } catch (e: Exception) {
                    println("error reading from client: ${e.message}")
                    exitProcess(1)
                }

                writer.responseWrite("+OK")
            }
        }
    }


    fun handleClient(conn: Socket) {

    }

    override fun close() {
        server.close()
    }
}

fun PrintWriter.responseWrite(response: String) {
    this.print(response + "\r\n")
    this.flush()
}
