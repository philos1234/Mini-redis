package command.handler

import command.CommandType
import command.CommandType.PING
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

class CommandHandlerMapper {

    private val log = LoggerFactory.getLogger(CommandHandlerMapper::class.java)

    private val handlerMethodMap: Map<CommandType, HandlerMethod> = mapOf(
        (PING to Ping()),

        )

    fun getHandler(commandType: String): HandlerMethod {
        val command = CommandType.from(commandType)
        log.info("Command Type ===  $command")

        val handler = handlerMethodMap[command] ?: let {
            log.error("Handler Mapping Error")
            throw IllegalArgumentException("Invalid Command")
        }

        return handler
    }
}