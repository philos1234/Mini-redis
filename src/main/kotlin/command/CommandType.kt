package command

import util.notNull

enum class CommandType {
    PING, QUIT,
    SET, GET,
    HSET, HGET, HGETALL,

    COMMAND;

    companion object {
        private val TYPES = entries.associateBy { it.name.lowercase() }
        fun from(commandType: String): CommandType {
            return TYPES[commandType.lowercase()].notNull { "Invalid Command Type : $commandType" }
        }
    }
}
