package command

import command.handler.HandlerMethodException
import util.notNullOrThrow

enum class CommandType {
    PING,
    SET, GET,
    HSET, HGET, HGETALL,

    COMMAND;

    companion object {
        private val TYPES = entries.associateBy { it.name.lowercase() }
        fun from(commandType: String): CommandType {
            return TYPES[commandType.lowercase()].notNullOrThrow { HandlerMethodException("Invalid Command Type : $commandType") }
        }
    }
}