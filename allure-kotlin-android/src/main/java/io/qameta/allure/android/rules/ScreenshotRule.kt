package io.qameta.allure.android.rules

import io.qameta.allure.android.allureScreenshot
import io.qameta.allure.kotlin.Allure
import org.junit.rules.*
import org.junit.runner.Description
import org.junit.runners.model.*
import kotlin.Result

/**
 * Makes screenshot of a device upon an end of a test case based on the specified [mode].
 * It is then added as an attachment of a test case (with name [screenshotName]).
 *
 * By default, it will take a screenshot at the end of every test case (whether it failed or finished successfully).
 */
class ScreenshotRule(
    private val mode: Mode = Mode.END,
    private val screenshotName: String = "end_screenshot"
) : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement = object : Statement() {
        override fun evaluate() {
            with(runCatching { base?.evaluate() }) {
                if (canTakeScreenshot(mode)) {
                    allureScreenshot(screenshotName)
                }
                getOrThrow()
            }
        }
    }

    private fun Result<*>.canTakeScreenshot(mode: Mode): Boolean =
        when (mode) {
            Mode.END -> true
            Mode.SUCCESS -> isSuccess
            Mode.FAILURE -> isFailure
        }

    enum class Mode {
        END,
        SUCCESS,
        FAILURE
    }

}

