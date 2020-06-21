package io.qameta.allure.android

import android.app.UiAutomation
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.IntRange
import androidx.test.platform.app.InstrumentationRegistry
import io.qameta.allure.kotlin.Allure
import java.io.ByteArrayOutputStream
import java.io.InputStream

private const val TAG = "AllureScreenshot"

/**
 * Takes a screenshot of a device in a given [quality] and attaches it to current step or test run.
 * Quality must be in range [0..100] or an exception will be thrown
 *
 * It uses [android.app.UiAutomation] under the hood to take the screenshot (just like the UiAutomator).
 *
 * @return true if screen shot is created and attached successfully, false otherwise
 */
@Suppress("unused")
fun allureScreenshot(name: String = "screenshot", @IntRange(from = 0, to = 100) quality: Int = 90): Boolean {
    require(quality in (0..100)) { "quality must be 0..100" }
    val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation ?: return false.also {
        Log.e(TAG, "UiAutomation is unavailable. Can't take the screenshot")
    }
    val inputStream = uiAutomation.takeScreenshot(quality) ?: return false.also {
        Log.e(TAG, "Failed to take the screenshot")
    }
    Allure.attachment(name = name, content = inputStream, type = "image/png", fileExtension = ".png")
    return true
}

private fun UiAutomation.takeScreenshot(quality: Int): InputStream? {
    val screenshot = takeScreenshot() ?: return null
    return ByteArrayOutputStream()
        .apply { screenshot.compress(Bitmap.CompressFormat.PNG, quality, this) }
        .toByteArray()
        .inputStream()
        .also { screenshot.recycle() }
}