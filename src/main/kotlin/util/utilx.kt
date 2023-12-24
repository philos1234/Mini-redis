package util


inline fun <T : Any> T?.notNull(lazyMessage: () -> Any): T = requireNotNull(this, lazyMessage)

fun <T : Any> T?.notNull(): T = requireNotNull(this)