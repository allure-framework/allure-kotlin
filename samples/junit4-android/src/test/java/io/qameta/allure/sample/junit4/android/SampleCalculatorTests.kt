package io.qameta.allure.sample.junit4.android

import io.qameta.allure.kotlin.Allure.parameter
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.junit4.AllureRunner
import io.qameta.allure.kotlin.junit4.DisplayName
import io.qameta.allure.kotlin.junit4.Tag
import org.hamcrest.CoreMatchers.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*

@RunWith(AllureRunner::class)
@Epic("Samples")
@DisplayName("SampleCalculator simpletests")
@Tag("Unit test")
class SampleCalculatorTest {

    @Test
    @DisplayName("addition test")
    @Feature("Addition")
    @Description("Checks if addition is implemented correctly")
    fun additionTest() {
        val x = 2
        val y = 4
        parameter("x", x)
        parameter("y", y)

        val actual = step("Add values") {
            SampleCalculator().add(x = x, y = y)
        }
        step("Verify correctness") {
            assertThat(actual, `is`(6))
        }

    }

    @Test
    @Feature("Subtraction")
    @DisplayName("subtraction test")
    @Description("Checks if subtractions is implemented correctly")
    fun subtractionTest() {
        val x = 2
        val y = 4
        parameter("x", x)
        parameter("y", y)

        val actual = step("Subtract values") {
            SampleCalculator().subtract(x = x, y = y)
        }
        step("Verify correctness") {
            assertThat(actual, `is`(-1))
        }

    }
}

