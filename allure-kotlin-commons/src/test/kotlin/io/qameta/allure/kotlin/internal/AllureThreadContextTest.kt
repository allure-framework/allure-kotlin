package io.qameta.allure.kotlin.internal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class AllureThreadContextTest {
    @Test
    fun shouldCreateEmptyContext() {
        val context = AllureThreadContext()
        assertThat(context.root).isNull()
        assertThat(context.current).isNull()
    }

    @Test
    fun shouldStart() {
        val context = AllureThreadContext()
        val first = UUID.randomUUID().toString()
        val second = UUID.randomUUID().toString()
        context.start(first)
        context.start(second)
        assertThat(context.root).isEqualTo(first)
        assertThat(context.current).isEqualTo(second)
    }

    @Test
    fun shouldClear() {
        val context = AllureThreadContext()
        val first = UUID.randomUUID().toString()
        val second = UUID.randomUUID().toString()
        context.start(first)
        context.start(second)
        context.clear()
        assertThat(context.root).isNull()
    }

    @Test
    fun shouldStop() {
        val context = AllureThreadContext()
        val first = UUID.randomUUID().toString()
        val second = UUID.randomUUID().toString()
        val third = UUID.randomUUID().toString()
        context.start(first)
        context.start(second)
        context.start(third)
        context.stop()
        assertThat(context.current).isEqualTo(second)
        context.stop()
        assertThat(context.current).isEqualTo(first)
        context.stop()
        assertThat(context.current).isNull()
    }

    @Test
    fun shouldIgnoreStopForEmpty() {
        val context = AllureThreadContext()
        context.stop()
        assertThat(context.root).isNull()
    }

    @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun shouldBeThreadSafe() {
        val context = AllureThreadContext()
        val threads = 1000
        val stepsCount = 200
        val service = Executors.newFixedThreadPool(threads)
        val tasks: MutableList<Callable<String>> =
            ArrayList()
        for (i in 0 until threads) {
            tasks.add(Callable<String> {
                for (j in 0 until stepsCount) {
                    context.start(UUID.randomUUID().toString())
                    context.stop()
                }
                context.current
            })
        }
        val base = "ROOT"
        context.start(base)
        val futures = service.invokeAll(tasks)
        for (future in futures) {
            val value = future.get()
            assertThat(value).isEqualTo(base)
        }
    }
}