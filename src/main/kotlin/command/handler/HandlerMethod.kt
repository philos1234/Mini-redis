package command.handler

import resp.Value
import resp.ValueType

fun interface HandlerMethod {
    fun invoke(args: List<Value>): Value
}


class Ping : HandlerMethod {
    override fun invoke(args: List<Value>): Value {
        if (args.isEmpty()) {
            return Value(type = ValueType.STRING, str = "Pong")
        }
        return Value(type = ValueType.STRING, str = args[0].bulk)
    }
}

class Init : HandlerMethod {
    override fun invoke(args: List<Value>): Value {
        return Value(type = ValueType.STRING, bulk = "OK")
    }
}


//fun set(args: Array<Value>): Value {
//    if (args.size != 2) {
//        return Value(type = "error", str = "ERR wrong number of arguments for 'set' command")
//    }
//
//    val key = args[0].bulk
//    val value = args[1].bulk
//
//    synchronized(sets) {
//        sets[key] = value
//    }
//
//    return Value(type = "string", str = "OK")
//}
//
//fun get(args: Array<Value>): Value {
//    if (args.size != 1) {
//        return Value(type = "error", str = "ERR wrong number of arguments for 'get' command")
//    }
//
//    val key = args[0].bulk
//
//    val value = synchronized(sets) { sets[key] }
//
//    return if (value != null) Value(type = "bulk", bulk = value)
//    else Value(type = "null")
//}
//
//val hsets = mutableMapOf<String, MutableMap<String, String>>()
//
//fun hset(args: Array<Value>): Value {
//    if (args.size != 3) {
//        return Value(type = "error", str = "ERR wrong number of arguments for 'hset' command")
//    }
//
//    val hash = args[0].bulk
//    val key = args[1].bulk
//    val value = args[2].bulk
//
//    synchronized(hsets) {
//        hsets.getOrPut(hash) { mutableMapOf() }[key] = value
//    }
//
//    return Value(type = "string", str = "OK")
//}
//
//fun hget(args: Array<Value>): Value {
//    if (args.size != 2) {
//        return Value(type = "error", str = "ERR wrong number of arguments for 'hget' command")
//    }
//
//    val hash = args[0].bulk
//    val key = args[1].bulk
//
//    val value = synchronized(hsets) { hsets[hash]?.get(key) }
//
//    return if (value != null) Value(type = "bulk", bulk = value)
//    else Value(type = "null")
//}
//
//fun hgetall(args: Array<Value>): Value {
//    if (args.size != 1) {
//        return Value(type = "error", str = "ERR wrong number of arguments for 'hgetall' command")
//    }
//
//    val hash = args[0].bulk
//
//    val values = synchronized(hsets) { hsets[hash] }
//        ?.flatMap { listOf(Value(type = "bulk", bulk = it.key), Value(type = "bulk", bulk = it.value)) }
//        ?.toTypedArray()
//        ?: arrayOf()
//
//    return Value(type = "array", array = values)
//}