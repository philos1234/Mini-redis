package command

import resp.Value

data class CommandOutput(
    val output: Value?,
    val error: String?
) {
    companion object {
        fun ofSuccess(output: Value?): CommandOutput {
            return CommandOutput(output = output, null)
        }

        fun ofError(error: String): CommandOutput {
            return CommandOutput(output = null, error)
        }
    }
}