package io.qameta.allure.android.runners

import android.os.Bundle
import androidx.multidex.MultiDex
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.AndroidJUnitRunner
import io.qameta.allure.android.AllureAndroidLifecycle
import io.qameta.allure.android.internal.isDeviceTest
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.junit4.AllureJunit4
import org.junit.runner.*
import org.junit.runner.manipulation.*
import org.junit.runner.notification.*

/**
 * Wrapper over [AndroidJUnit4] that attaches the [AllureJunit4] listener
 */
class AllureAndroidJUnit4(clazz: Class<*>) : Runner(), Filterable, Sortable {

    private val delegate = AndroidJUnit4(clazz)

    override fun run(notifier: RunNotifier?) {
        createListener()?.let {
            notifier?.addListener(it)
        }
        delegate.run(notifier)
    }

    private fun createListener(): RunListener? =
        if (isDeviceTest()) {
            createDeviceListener()
        } else {
            createRobolectricListener()
        }

    /**
     * Creates listener for the tests running on a device.
     *
     * In instrumentation tests the listeners are shared between the class runners,
     * hence extra logic has to be put in place, to avoid attaching the listener more than once.
     *
     * Check is made whether [AllureAndroidLifecycle] has been specified as [Allure.lifecycle],
     * if so it means that in one way or another the listener has already been attached.
     */
    private fun createDeviceListener(): RunListener? {
        if (Allure.lifecycle == AllureAndroidLifecycle) return null

        Allure.lifecycle = AllureAndroidLifecycle
        return AllureJunit4(AllureAndroidLifecycle)
    }

    /**
     * Creates listener for tests running in an emulated Robolectric environment.
     *
     * The listeners are not shared between class runners, hence they have to be added to each class runner separately.
     */
    private fun createRobolectricListener(): RunListener? = AllureJunit4()

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

