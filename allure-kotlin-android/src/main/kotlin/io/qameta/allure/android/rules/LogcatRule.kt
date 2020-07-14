package io.qameta.allure.android.rules

import android.os.Build
import android.util.Log
import androidx.test.uiautomator.UiDevice
import io.qameta.allure.android.internal.uiDevice
import io.qameta.allure.kotlin.Allure
import org.junit.rules.*
import org.junit.runner.*
import org.junit.runners.model.*

/**
 * Clears logcat before each test and dumps the logcat as an attachment after test failure.
 *
 * Available since API 21, no effect for other versions.
 */
class LogcatRule(private val fileName: String = "logcat-dump") : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement = object : Statement() {
        override fun evaluate() {
            try {
                clear()
                base?.evaluate()
            } catch (throwable: Throwable) {
                dump()
                throw throwable
            }
        }
    }

    private fun clear() {
        val uiDevice = uiDevice ?: return Unit.also {
            Log.e(TAG, "UiDevice is unavailable. Clearing logs failed.")
        }
        uiDevice.executeShellCommandSafely("logcat -c")
    }

    private fun dump() {
        val uiDevice = uiDevice ?: return Unit.also {
            Log.e(TAG, "UiDevice is unavailable. Dumping logs failed.")
        }
        val output = uiDevice.executeShellCommandSafely("logcat -d") ?: return
        Allure.attachment(
            name = fileName,
            content = output,
            type = "text/plain",
            fileExtension = ".txt"
        )
    }

    private fun UiDevice.executeShellCommandSafely(cmd: String): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
        return executeShellCommand(cmd)
    }

    companion object {
        private val TAG = LogcatRule::class.java.simpleName
    }
}