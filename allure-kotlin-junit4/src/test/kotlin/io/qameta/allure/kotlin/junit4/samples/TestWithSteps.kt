package io.qameta.allure.kotlin.junit4.samples

import io.qameta.allure.kotlin.Allure.step
import org.junit.Test

class TestWithSteps {
    @Test
    fun testWithSteps() {
        step("step1")
        step("step2")
        step("step3")
    }
}