package io.qameta.allure.android.runners

import android.os.Bundle
import androidx.multidex.MultiDex
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.AndroidJUnitRunner
import io.qameta.allure.android.AllureAndroidLifecycle
import io.qameta.allure.android.internal.isDeviceTest
import io.qameta.allure.android.internal.requestExternalStoragePermissions
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.junit4.AllureJunit4
import org.junit.runner.*
import org.junit.runner.manipulation.*
import org.junit.runner.notification.*

/**
 * Wrapper over [AndroidJUnit4] that attaches the [AllureJunit4] listener and
 * grants external storage permissions for tests running on a device (required for the test results to be saved).
 */
class AllureAndroidJUnit4(clazz: Class<*>) : Runner(), Filterable, Sortable {

    private val delegate = AndroidJUnit4(clazz)

    override fun run(notifier: RunNotifier?) {
        notifier?.addListener(createAllureListener())
        if (isDeviceTest()) {
            requestExternalStoragePermissions()
        }
        delegate.run(notifier)
    }

    private fun createAllureListener(): AllureJunit4 {
        return with(AllureAndroidLifecycle) {
            Allure.lifecycle = this
            AllureJunit4(this)
        }
    }

    override fun getDescription(): Description = delegate.description

    override fun filter(filter: Filter?) = delegate.filter(filter)

    override fun sort(sorter: Sorter?) = delegate.sort(sorter)

}

/**
 * Custom [AndroidJUnitRunner] that setups [AllureAndroidLifecycle] and attaches [AllureJunit4] listener.
 * It also automatically grants the external storage permission (required for the test results to be saved).
 */
open class AllureAndroidJUnitRunner : AndroidJUnitRunner() {

    override fun onCreate(arguments: Bundle) {
        Allure.lifecycle = AllureAndroidLifecycle
        val listenerArg = listOfNotNull(
            arguments.getCharSequence("listener"),
            AllureJunit4::class.java.name
        ).joinToString(separator = ",")
        arguments.putCharSequence("listener", listenerArg)
        super.onCreate(arguments)
    }

    override fun onStart() {
        super.onStart()
        requestExternalStoragePermissions()
    }

}

/**
 * [AllureAndroidJUnitRunner] that additionally patches the instrumentation context using [MultiDex]
 */
open class MultiDexAllureAndroidJUnitRunner : AllureAndroidJUnitRunner() {

    override fun onCreate(arguments: Bundle) {
        MultiDex.installInstrumentation(context, targetContext)
        super.onCreate(arguments)
    }
}

