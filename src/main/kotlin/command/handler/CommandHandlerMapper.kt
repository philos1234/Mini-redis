package command.handler

import command.CommandType
import command.CommandType.*
import util.notNullOrThrow

class CommandHandlerMapper {

    val handlerMethodMap: Map<CommandType, HandlerMethod>

    init {

        handlerMethodMap = mapOf(
            (PING to Ping()),
            (COMMAND to Init()),
            (SET to Set()),
            (GET to Get()),
            (HSET to HSet()),
            (HGET to HGet()),
            (HGETALL to HGetAll())
        )
    }

    fun getHandler(commandType: String): HandlerMethod {
        val command = CommandType.from(commandType)
        return handlerMethodMap[command].notNullOrThrow { HandlerMethodException("Could not find HandlerMethod. Command Type: $commandType") }
    }
}