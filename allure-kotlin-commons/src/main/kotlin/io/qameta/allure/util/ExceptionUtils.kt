package io.qameta.allure.util

object ExceptionUtils {
    @JvmStatic
    fun <T : Throwable> sneakyThrow(throwable: Throwable): Nothing {
        throw throwable as T
    }
}