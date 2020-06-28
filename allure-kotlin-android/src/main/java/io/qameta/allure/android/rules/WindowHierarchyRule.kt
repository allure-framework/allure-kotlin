package io.qameta.allure.android.rules

import android.util.Log
import androidx.test.uiautomator.UiDevice
import io.qameta.allure.android.internal.uiDevice
import io.qameta.allure.kotlin.Allure
import org.junit.rules.*
import org.junit.runner.*
import org.junit.runners.model.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * Dumps window hierarchy when the test fails and adds it as an attachment to allure results.
 * Available since API 18, no effect for other versions.
 */
class WindowHierarchyRule(private val fileName: String = "window-hierarchy") : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement = object : Statement() {
        override fun evaluate() {
            runCatching { base?.evaluate() }
                .onFailure { dumpWindowHierarchy() }
                .getOrThrow()
        }
    }

    private fun dumpWindowHierarchy() {
        val uiDevice = uiDevice ?: return Unit.also {
            Log.e(TAG, "UiAutomation is unavailable. Dumping window hierarchy failed.")
        }

        Allure.attachment(
            name = fileName,
            type = "text/xml",
            fileExtension = ".xml",
            content = uiDevice.dumpWindowHierarchy()
        )
    }

    private fun UiDevice.dumpWindowHierarchy(): ByteArrayInputStream =
        ByteArrayOutputStream()
            .apply {
                waitForIdle(TimeUnit.SECONDS.toMillis(5))
                dumpWindowHierarchy(this)
            }
            .toByteArray()
            .inputStream()

    companion object {
        private val TAG = WindowHierarchyRule::class.java.simpleName
    }

}
