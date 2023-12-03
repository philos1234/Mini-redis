package resp

import java.io.IOException
import java.io.InputStream


class RespReader private constructor(private val inputStream: InputStream) {

    fun readLine(): Pair<ByteArray?, Int> {
        val lineBuffer = mutableListOf<Byte>()
        var n = 0
        try {
            while (true) {
                val b = inputStream.read()
                if (b == -1) {
                    throw IOException("End of stream")
                }
                n++
                lineBuffer.add(b.toByte())

                if (lineBuffer.size >= 2 && b.toByte() == '\n'.code.toByte()) {
                    break
                }

            }
        } catch (e: IOException) {
            return Pair(null, n)
        }


        val line = lineBuffer.dropLast(2).toByteArray() // CRLF 제거
        return Pair(line, n)
    }


    fun readInteger(): Pair<Int, Int> {
        val (line, length) = readLine()
        if (line == null) {
            throw NumberFormatException("Invalid integer format: line is null")
        }
        val numberString = line.decodeToString()
        val number =
            numberString.toIntOrNull() ?: throw NumberFormatException("Invalid integer format: '$numberString'")

        return Pair(number, length)
    }

    fun read(): Value {
        val type = try {
            inputStream.read()
        } catch (e: Exception) {
            return Value(ValueType.UNKNOWN)
        }

        return when (type.toChar()) {
            ValueType.ARRAY.typeChar -> readArray()
            ValueType.BULK.typeChar -> readBulk()
            else -> {
                println("Unknown type: ${type.toChar()}")
                Value(ValueType.UNKNOWN)
            }
        }
    }


    fun readArray(): Value {
        val v = Value(ValueType.ARRAY, array = mutableListOf())

        // 배열 길이 읽기
        val (len, _) = readInteger()

        // 배열의 각 요소를 파싱하고 읽기
        for (i in 0 until len) {
            val value = read()
            // 파싱된 값을 배열에 추가
            (v.array as MutableList).add(value)
        }
        return v
    }

    fun readBulk(): Value {
        return try {
            val (length, _) = readInteger()
            val bulk = ByteArray(length)
            inputStream.read(bulk)

            val bulkValue = String(bulk)

            // 후행 CRLF 읽기
            readLine()
            Value(ValueType.BULK, bulkValue)
        } catch (e: Exception) {
            return Value.ofEmpty(ValueType.BULK)
        }
    }

    companion object {
        fun of(inputStream: InputStream): RespReader {
            return RespReader(inputStream)
        }
    }
}