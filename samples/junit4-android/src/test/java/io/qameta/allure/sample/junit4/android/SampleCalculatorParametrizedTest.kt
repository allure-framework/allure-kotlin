package io.qameta.allure.sample.junit4.android

import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.junit4.AllureParametrizedRunner
import io.qameta.allure.kotlin.junit4.DisplayName
import io.qameta.allure.kotlin.junit4.Tag
import org.hamcrest.*
import org.junit.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(AllureParametrizedRunner::class)
@Epic("Samples")
@DisplayName("SampleCalculator Parameterized tests")
@Tag("Unit test")
class SampleCalculatorParametrizedTest(val x: Int, val y: Int, val expected: Int) {

    @Test
    @Feature("Addition")
    @DisplayName("addition parametrized test")
    fun additionTest() {
        Allure.parameter("x", x)
        Allure.parameter("y", y)

        val actual = Allure.step("Add values") {
            SampleCalculator().add(x = x, y = y)
        }
        Allure.step("Verify correctness") {
            Assert.assertThat(
                actual,
                CoreMatchers.`is`(expected)
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = arrayOf(
            arrayOf(2, 2, 4),
            arrayOf(2, 4, 6),
            arrayOf(2, 10, 12)
        )

    }
}