package io.qameta.allure.android.internal

import android.annotation.SuppressLint
import android.os.Build
import androidx.test.runner.permission.PermissionRequester

/**
 * Gives information about the environment in which the tests are running:
 *  - @return true for Android device (real device or emulator)
 *  - @return false for emulated environment of Robolectric
 *
 * This is the same logic as present in [androidx.test.ext.junit.runners.AndroidJUnit4] for resolving class runner.
 */
@SuppressLint("DefaultLocale")
internal fun isDeviceTest(): Boolean =
    System.getProperty("java.runtime.name")?.toLowerCase()?.contains("android") ?: false

internal fun requestExternalStoragePermissions() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

    with(PermissionRequester()) {
        addPermissions("android.permission.WRITE_EXTERNAL_STORAGE")
        addPermissions("android.permission.READ_EXTERNAL_STORAGE")
        requestPermissions()
    }
}
