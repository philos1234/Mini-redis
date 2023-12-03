package resp

enum class ValueType(val typeChar: Char) {
    STRING('+'), ERROR('-'), INTEGER(':'), BULK('$'), ARRAY('*'), UNKNOWN(' ');
}

data class Value(
    val type: ValueType,
    val str: String = "",
    val num: Int = 0,
    val bulk: String = "",
    val array: List<Value> = emptyList()
) {
    companion object {
        fun ofEmpty(type: ValueType): Value {
            return Value(type = type)
        }
    }
}