package io.qameta.allure.kotlin.junit4.samples

import io.qameta.allure.kotlin.Allure.step
import org.junit.Test

class TestWithTimeout {
    @Test(timeout = 100)
    fun testWithSteps() {
        step("Step 1")
        step("Step 2")
    }
}