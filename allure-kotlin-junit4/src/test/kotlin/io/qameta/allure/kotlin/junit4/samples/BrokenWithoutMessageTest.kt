package io.qameta.allure.kotlin.junit4.samples

import org.junit.Test

class BrokenWithoutMessageTest {
    @Test
    @Throws(Exception::class)
    fun brokenWithoutMessageTest() {
        throw RuntimeException()
    }
}