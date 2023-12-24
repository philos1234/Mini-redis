package command.handler

open class RedisException(message: String) : RuntimeException() {
    val errorMessage: String = message
}

class HandlerMethodException(message: String) : RedisException(message) {
}