package command.handler

import command.CommandType
import command.CommandType.*
import org.slf4j.LoggerFactory
import util.notNull

class CommandHandlerMapper {

    private val log = LoggerFactory.getLogger(CommandHandlerMapper::class.java)

    private val handlerMethodMap: Map<CommandType, HandlerMethod>

    init {
        handlerMethodMap = mapOf(
            (PING to Ping()),
            (COMMAND to Init()),
            (SET to Set()),
            (GET to Get())
        )
    }

    fun getHandler(commandType: String): HandlerMethod {
        val command = CommandType.from(commandType)
        return handlerMethodMap[command].notNull { "Could not find HandlerMethod. Command Type: $commandType" }
    }
}