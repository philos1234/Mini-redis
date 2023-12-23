package command

import resp.Value
import resp.ValueType

data class CommandOutput(
    val output: Value
) {
    companion object {
        fun ofSuccess(output: Value): CommandOutput {
            return CommandOutput(output = output)
        }

        fun ofError(error: String): CommandOutput {
            return CommandOutput(output = Value(type = ValueType.ERROR, error))
        }
    }
}