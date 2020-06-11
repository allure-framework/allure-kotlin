package io.qameta.allure.junit4.samples

import org.junit.Test

class BrokenTest {
    @Test
    @Throws(Exception::class)
    fun brokenTest() {
        throw RuntimeException("Hello, everybody")
    }
}