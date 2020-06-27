package io.qameta.allure.android

import android.os.Environment
import io.qameta.allure.android.internal.isDeviceTest
import io.qameta.allure.kotlin.AllureLifecycle
import io.qameta.allure.kotlin.FileSystemResultsWriter
import io.qameta.allure.kotlin.util.PropertiesUtils
import java.io.File

object AllureAndroidLifecycle : AllureLifecycle(writer = FileSystemResultsWriter(obtainResultsDirectory()))

/**
 * Obtains results directory as a [File] reference. It takes into consideration the results directory specified via **allure.results.directory** property.
 * For device tests it uses the external storage to save the results.
 */
private fun obtainResultsDirectory(): File {
    val defaultAllurePath = PropertiesUtils.resultsDirectoryPath
    return when {
        isDeviceTest() -> File(
            Environment.getExternalStorageDirectory(),
            defaultAllurePath
        )
        else -> File(defaultAllurePath)
    }
}