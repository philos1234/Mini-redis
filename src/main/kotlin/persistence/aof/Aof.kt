package persistence.aof

import resp.RespReader
import resp.Value
import java.io.BufferedReader
import java.io.File


class Aof private constructor(fileName: String) : AutoCloseable {
    private lateinit var file: File
    private val bufferedReader: BufferedReader = file.bufferedReader()
    private val aofLock = Any()


    init {
        createFileIfNeeded(fileName)
    }

    fun write(value: Value) {
        TODO()
//        synchronized(aofLock) {
//            val executor = AofExecutor.get()
//
//            executor.execute(value)
//        }
    }

    fun read(process: (vale: Value) -> Unit) {
        synchronized(aofLock) {
            val reader = RespReader.of(file.inputStream())
            while (true) {
                val value = reader.readValueOrNull() ?: break
                process(value)
            }
        }
    }


    override fun close() {
        bufferedReader.close()
    }

    private fun createFileIfNeeded(fileName: String): File {
        file = File(FILE_PATH + fileName)
        return if (file.createNewFile()) {
            file
        } else file
    }

    companion object {
        private const val FILE_PATH = "classpath:"
        private var INSTANCE: Aof? = null
        private val lock = Any()


        fun create(fileName: String): Aof {
            return synchronized(lock) {
                INSTANCE ?: Aof(fileName).also { INSTANCE = it }
            }
        }

        fun get(): Aof {
            return requireNotNull(INSTANCE) { "aof instance must not be null" }
        }
    }
}