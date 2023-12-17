package command.handler

import command.CommandOutput
import resp.Value
import resp.ValueType

fun interface HandlerMethod {
    fun invoke(): CommandOutput
}


class Ping : HandlerMethod {
    override fun invoke(): CommandOutput {
        return CommandOutput.ofSuccess(  Value(type = ValueType.STRING, bulk = "Pong"))
    }
}