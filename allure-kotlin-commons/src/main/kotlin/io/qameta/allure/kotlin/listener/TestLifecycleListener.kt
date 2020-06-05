package io.qameta.allure.kotlin.listener

import io.qameta.allure.kotlin.model.TestResult

/**
 * Listener that notifies about Allure Lifecycle events.
 *
 * @since 2.0
 */
interface TestLifecycleListener : LifecycleListener {
    fun beforeTestSchedule(result: TestResult) { //do nothing
    }

    fun afterTestSchedule(result: TestResult) { //do nothing
    }

    fun beforeTestUpdate(result: TestResult) { //do nothing
    }

    fun afterTestUpdate(result: TestResult) { //do nothing
    }

    fun beforeTestStart(result: TestResult) { //do nothing
    }

    fun afterTestStart(result: TestResult) { //do nothing
    }

    fun beforeTestStop(result: TestResult) { //do nothing
    }

    fun afterTestStop(result: TestResult) { //do nothing
    }

    fun beforeTestWrite(result: TestResult) { //do nothing
    }

    fun afterTestWrite(result: TestResult) { //do nothing
    }
}