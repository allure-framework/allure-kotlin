package io.qameta.allure.android.listeners

import io.qameta.allure.android.internal.requestExternalStoragePermissions
import org.junit.runner.*
import org.junit.runner.notification.*

class ExternalStoragePermissionsListener : RunListener() {
    override fun testRunStarted(description: Description?) {
        requestExternalStoragePermissions()
    }
}