package command.handler

import command.CommandOutput
import org.slf4j.LoggerFactory
import resp.Value
import resp.ValueType

private val memoryMaps = mutableMapOf<String, String>()
private val hashMemoryMaps = mutableMapOf<String, MutableMap<String, String>>()

fun interface HandlerMethod {
    fun invoke(args: List<Value>): CommandOutput
}

abstract class DefaultHandlerMethod : HandlerMethod {

    final override fun invoke(args: List<Value>): CommandOutput {
        return try {
            val output = invokeInternal(args)
            return CommandOutput.ofSuccess(output)
        } catch (e: HandlerMethodException) {
            log.error("Method Handling Error", e)
            CommandOutput(Value(type = ValueType.ERROR, str = e.errorMessage))
        } catch (e: Exception) {
            log.error("Method Handling Error", e)
            CommandOutput.ofError(e.message ?: "System Error")
        }
    }

    abstract fun invokeInternal(args: List<Value>): Value

    companion object {
        private val log = LoggerFactory.getLogger(DefaultHandlerMethod::class.java)
    }
}


class Init : DefaultHandlerMethod() {
    override fun invokeInternal(args: List<Value>): Value {
        return Value(type = ValueType.STRING, bulk = "OK")
    }
}


class Ping : DefaultHandlerMethod() {
    override fun invokeInternal(args: List<Value>): Value {
        if (args.isEmpty()) {
            return Value(type = ValueType.STRING, str = "Pong")
        }
        return Value(type = ValueType.STRING, str = args[0].bulk)
    }
}

class Set : DefaultHandlerMethod() {

    override fun invokeInternal(args: List<Value>): Value {
        if (args.size != 2) {
            throw HandlerMethodException("ERR wrong number of arguments for 'set' command")
        }
        val key = args[0].bulk
        val value = args[1].bulk

        synchronized(memoryMaps) {
            memoryMaps[key] = value
        }

        return Value(type = ValueType.STRING, str = "OK")
    }
}

class Get : DefaultHandlerMethod() {

    override fun invokeInternal(args: List<Value>): Value {
        if (args.size != 1) {
            throw HandlerMethodException("ERR wrong number of arguments for 'get' command")
        }

        val key = args[0].bulk
        val value = synchronized(memoryMaps) { memoryMaps[key] }

        return if (value != null) Value(type = ValueType.BULK, bulk = value) else Value.ofEmpty(ValueType.BULK)
    }
}

class HSet : DefaultHandlerMethod() {
    override fun invokeInternal(args: List<Value>): Value {
        if (args.size != 3) {
            throw HandlerMethodException("ERR wrong number of arguments for 'hset' command")
        }

        val hash = args[0].bulk
        val key = args[1].bulk
        val value = args[2].bulk

        synchronized(hashMemoryMaps) {
            hashMemoryMaps.getOrPut(hash) {
                mutableMapOf(key to value)
            }
        }

        return Value(ValueType.STRING, str = "OK")
    }
}

class HGet : DefaultHandlerMethod() {
    override fun invokeInternal(args: List<Value>): Value {
        if (args.size != 2) {
            throw HandlerMethodException("ERR wrong number of arguments for 'hget' command")
        }

        val hash = args[0].bulk
        val key = args[1].bulk

        val value = synchronized(hashMemoryMaps) {
            hashMemoryMaps[hash]?.get(key)
                ?: throw HandlerMethodException("ERR wrong number of arguments for 'hget' command")
        }

        return Value(ValueType.BULK, bulk = value)
    }
}