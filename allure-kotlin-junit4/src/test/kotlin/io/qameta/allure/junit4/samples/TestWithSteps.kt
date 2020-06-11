package io.qameta.allure.junit4.samples

import io.qameta.allure.Allure.step
import org.junit.Test

class TestWithSteps {
    @Test
    fun testWithSteps() {
        step("step1")
        step("step2")
        step("step3")
    }
}