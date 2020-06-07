package io.qameta.allure.kotlin.test

import java.util.function.Function

fun <T> function(block: (T) -> Any?): Function<T, Any?> = Function { block(it) }