package command

import util.notNull
import java.lang.reflect.Method

enum class CommandType {
    PING, QUIT,
    SET, GET;

    companion object {
        private val TYPES = entries.associateBy { it.name.lowercase() }
        fun from(commandType: String): CommandType {
            return TYPES[commandType.lowercase()].notNull { "Invalid Command Type : $commandType" }
        }
    }
}
