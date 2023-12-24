package command.handler

import command.CommandType
import command.CommandType.*
import util.notNull

class CommandHandlerMapper {

    private val handlerMethodMap: Map<CommandType, HandlerMethod>

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
        return handlerMethodMap[command].notNull { "Could not find HandlerMethod. Command Type: $commandType" }
    }
}