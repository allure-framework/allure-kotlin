@file:JvmName("LogUtils")

package io.qameta.allure.kotlin.util

import java.util.logging.Level
import java.util.logging.Logger

internal inline fun <reified T : Any> loggerFor(): Logger = Logger.getLogger(T::class.java.name)

internal fun Logger.debug(message: String, throwable: Throwable? = null) {
    log(Level.FINE, message, throwable)
}

internal fun Logger.debug(message: String, vararg params: Any?) {
    log(Level.FINE, message, params)
}

internal fun Logger.trace(message: String) {
    log(Level.FINE, message)
}

internal fun Logger.error(message: String, throwable: Throwable? = null) {
    log(Level.SEVERE, message, throwable)
}

internal fun Logger.error(message: String, vararg params: Any?) {
    log(Level.SEVERE, message, params)
}

internal fun Logger.warn(message: String, throwable: Throwable? = null) {
    log(Level.WARNING, message, throwable)
}