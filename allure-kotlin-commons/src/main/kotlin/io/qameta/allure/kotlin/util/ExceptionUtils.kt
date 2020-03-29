package io.qameta.allure.kotlin.util

object ExceptionUtils {
    @JvmStatic
    fun <T : Throwable> sneakyThrow(throwable: Throwable): Nothing {
        throw throwable as T
    }
}