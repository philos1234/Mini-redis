package resp

import java.io.OutputStream
import java.io.PrintWriter

class RespWriter private constructor(val writer: PrintWriter) {

    fun write(response: Value) {
        val byte = marshal(response)
        writer.print(byte.map { it.toInt().toChar() }.toCharArray())
        writer.flush()
    }

    private fun marshal(response: Value): ByteArray {
        return when (response.type) {
            ValueType.ARRAY -> {
                marshalArray(response.array)
            }

            ValueType.BULK -> {
                marshalBulk(response.bulk)
            }

            ValueType.STRING -> {
                marshalString(response.str)
            }

            ValueType.ERROR -> {
                marshallError(response)
            }

            ValueType.UNKNOWN -> {
                marshallNull()
            }

            ValueType.INTEGER -> {
                return ByteArray(0)
            }

            ValueType.EOF -> throw IllegalArgumentException("Marshal EOF is not supported")
        }
    }

    private fun marshalString(stringResponse: String): ByteArray {
        val bytes = ByteArray(1 + stringResponse.length + 2)
        bytes[0] = ValueType.STRING.convertToCharacterByte()
        stringResponse.toByteArray().copyInto(bytes, 1)
        bytes[bytes.lastIndex - 1] = '\r'.code.toByte()
        bytes[bytes.lastIndex] = '\n'.code.toByte()
        return bytes
    }

    private fun marshalBulk(bulkResponse: String): ByteArray {
        val bulkLength = bulkResponse.length
        val bulkLengthStr = bulkLength.toString()

        // BULK 상수 + 길이 + "\r\n" + bulk 내용 + "\r\n"
        val bytes = ByteArray(1 + bulkLengthStr.length + 2 + bulkLength + 2)
        var cursor = 0

        bytes[cursor++] = ValueType.BULK.convertToCharacterByte()
        bulkLengthStr.toByteArray().forEach { bytes[cursor++] = it }
        bytes[cursor++] = '\r'.code.toByte()
        bytes[cursor++] = '\n'.code.toByte()
        bulkResponse.toByteArray().copyInto(bytes, cursor)
        cursor += bulkLength
        bytes[cursor++] = '\r'.code.toByte()
        bytes[cursor] = '\n'.code.toByte()

        return bytes
    }

    private fun marshalArray(arrayResponse: List<Value>): ByteArray {
        val len = arrayResponse.size
        val lenStr = len.toString()
        var bytes = ByteArray(1 + lenStr.length + 2) // ARRAY 상수 + 길이 + "\r\n"

        var cursor = 0
        bytes[cursor++] = ValueType.ARRAY.convertToCharacterByte()// ARRAY 상수
        lenStr.toByteArray().forEach { bytes[cursor++] = it }
        bytes[cursor++] = '\r'.code.toByte()
        bytes[cursor++] = '\n'.code.toByte()

        arrayResponse.forEach { value ->
            val valueBytes = marshal(value)
            bytes += valueBytes
        }

        return bytes
    }

    private fun marshallError(value: Value): ByteArray {
        val strBytes = value.str.toByteArray() ?: ByteArray(0)
        val bytes = ByteArray(1 + strBytes.size + 2) // ERROR 상수 + 문자열 + "\r\n"
        var cursor = 0

        bytes[cursor++] = ValueType.ERROR.convertToCharacterByte() // ERROR 상수
        strBytes.forEach { bytes[cursor++] = it }
        bytes[cursor++] = '\r'.code.toByte()
        bytes[cursor] = '\n'.code.toByte()

        return bytes
    }

    private fun marshallNull(): ByteArray {
        return "\$-1\r\n".toByteArray()
    }

    companion object {
        fun of(outputStream: OutputStream): RespWriter {
            return RespWriter(PrintWriter(outputStream, false))
        }

    }
}