package util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


inline fun <T : Any> T?.notNull(lazyMessage: () -> Any): T = requireNotNull(this, lazyMessage)

inline fun <T : Any> T?.notNullOrThrow(exception: () -> Exception): T =
    notNullOrThrow(this, exception)

@OptIn(ExperimentalContracts::class)
inline fun <T : Any> T?.notNullOrThrow(value: T?, exception: () -> Exception): T {
    contract {
        returns() implies (value != null)
    }

    if (this == null) {
        throw exception()
    } else {
        return this
    }
}


fun <T : Any> T?.notNull(): T = requireNotNull(this)