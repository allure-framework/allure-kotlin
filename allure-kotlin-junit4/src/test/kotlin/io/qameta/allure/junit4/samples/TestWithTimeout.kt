package io.qameta.allure.junit4.samples

import io.qameta.allure.Allure.step
import org.junit.Test

class TestWithTimeout {
    @Test(timeout = 100)
    fun testWithSteps() {
        step("Step 1")
        step("Step 2")
    }
}