package command.handler

import command.CommandType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.containsKeys
import strikt.assertions.isA
import strikt.assertions.isNotNull

class CommandHandlerMapperTest {

    private val sut = CommandHandlerMapper()


    @Test
    fun get_HandlerMethod_Given_PING_CommandType_Name() {
        val commandType = CommandType.PING

        val actual = sut.getHandler(commandType.name)

        expectThat(actual) {
            isNotNull()
            isA<Ping>()
        }
    }

    @Test
    fun throw_HandlerMethodException_When_Given_Invalid_CommandType() {
        expectThrows<HandlerMethodException> {
            sut.getHandler("INVALID")
        }
    }

    @Test
    fun handlerMap_Should_Contains_All_CommandTypes_Handlers() {
        val handlerMap = sut.handlerMethodMap
        expectThat(handlerMap) {
            containsKeys(*CommandType.entries.toTypedArray())
        }
    }
}