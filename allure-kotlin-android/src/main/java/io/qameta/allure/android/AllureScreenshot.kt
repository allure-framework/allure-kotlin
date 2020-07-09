package io.qameta.allure.android

import android.util.Log
import androidx.annotation.IntRange
import androidx.test.uiautomator.UiDevice
import io.qameta.allure.android.internal.createTemporaryFile
import io.qameta.allure.android.internal.uiDevice
import io.qameta.allure.kotlin.Allure
import java.io.InputStream

private const val TAG = "AllureScreenshot"

/**
 * Takes a screenshot of a device in a given [quality] & [scale], after that attaches it to current step or test run.
 * Quality must be in range [0..100] or an exception will be thrown.
 *
 * It uses [androidx.test.uiautomator.UiDevice] to take the screenshot.
 *
 * @return true if screen shot is created and attached successfully, false otherwise
 */
@Suppress("unused")
fun allureScreenshot(
    name: String = "screenshot",
    @IntRange(from = 0, to = 100) quality: Int = 90,
    scale: Float = 1.0f
): Boolean {
    require(quality in (0..100)) { "quality must be 0..100" }
    val uiDevice = uiDevice ?: return false.also {
        Log.e(TAG, "UiAutomation is unavailable. Can't take the screenshot")
    }
    val inputStream = uiDevice.takeScreenshot(scale, quality) ?: return false.also {
        Log.e(TAG, "Failed to take the screenshot")
    }
    Allure.attachment(name = name, content = inputStream, type = "image/png", fileExtension = ".png")
    return true
}

private fun UiDevice.takeScreenshot(scale: Float, quality: Int): InputStream? {
    val tempFile = createTemporaryFile(prefix = "screenshot")
    if (!takeScreenshot(tempFile, scale, quality)) return null
    return tempFile.inputStream()
}