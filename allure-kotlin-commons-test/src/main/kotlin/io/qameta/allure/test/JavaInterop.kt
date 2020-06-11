package io.qameta.allure.test

import java.util.function.Function

fun <T> function(block: (T) -> Any?): Function<T, Any?> = Function { block(it) }