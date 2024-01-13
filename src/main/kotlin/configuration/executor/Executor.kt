package configuration.executor

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AofExecutor private constructor(
    private val executor: ExecutorService
) {
    fun execute(runnable: Runnable) {
        executor.submit(runnable)
    }


    fun sync() {
        TODO()
    }


    companion object {
        private var INSTANCE: AofExecutor? = null
        private val lock = Any()


        fun create(): AofExecutor {
            return synchronized(lock) {
                INSTANCE ?: AofExecutor(
                    executor = Executors.newSingleThreadExecutor()
                ).also { INSTANCE = it }
            }
        }

        fun get(): AofExecutor {
            return INSTANCE ?: create()
        }
    }
}