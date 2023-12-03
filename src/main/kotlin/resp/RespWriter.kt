package resp

import java.io.PrintWriter

class RespWriter(val writer: PrintWriter) {
    fun marshal(response: Value): ByteArray {
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
        }
    }

    private fun marshalString(stringResponse: String): ByteArray {
        val bytes = ByteArray(stringResponse.length + 2)
        bytes[0] = ValueType.STRING.ordinal.toByte() //
        stringResponse.toByteArray().copyInto(bytes, 1)
        bytes[bytes.lastIndex - 1] = '\r'.toByte()
        bytes[bytes.lastIndex] = '\n'.toByte()
        return bytes
    }

    private fun marshalBulk(bulkResponse: String): ByteArray {
        val bulkLength = bulkResponse.length
        val bulkLengthStr = bulkLength.toString()

        // BULK 상수 + 길이 + "\r\n" + bulk 내용 + "\r\n"
        val bytes = ByteArray(1 + bulkLengthStr.length + 2 + bulkLength + 2)
        var cursor = 0

        bytes[cursor++] = ValueType.BULK.ordinal.toByte()
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
        bytes[cursor++] = ValueType.ARRAY.ordinal.toByte() // ARRAY 상수
        lenStr.toByteArray().forEach { bytes[cursor++] = it }
        bytes[cursor++] = '\r'.code.toByte()
        bytes[cursor++] = '\n'.code.toByte()

        arrayResponse.forEach { value ->
            val valueBytes = marshal(value)
            bytes += valueBytes
        }

        return bytes
    }

    fun marshallError(value: Value): ByteArray {
        val strBytes = value.str.toByteArray() ?: ByteArray(0)
        val bytes = ByteArray(1 + strBytes.size + 2) // ERROR 상수 + 문자열 + "\r\n"
        var cursor = 0

        bytes[cursor++] = ValueType.ERROR.ordinal.toByte() // ERROR 상수
        strBytes.forEach { bytes[cursor++] = it }
        bytes[cursor++] = '\r'.code.toByte()
        bytes[cursor] = '\n'.code.toByte()

        return bytes
    }

    fun marshallNull(): ByteArray {
        return "\$-1\r\n".toByteArray()
    }


}