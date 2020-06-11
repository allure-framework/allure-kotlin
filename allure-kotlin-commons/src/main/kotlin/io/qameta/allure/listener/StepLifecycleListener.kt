package io.qameta.allure.listener

import io.qameta.allure.model.StepResult

/**
 * Notifies about Allure step lifecycle events.
 *
 * @since 2.0
 */
interface StepLifecycleListener : LifecycleListener {
    fun beforeStepStart(result: StepResult) { //do nothing
    }

    fun afterStepStart(result: StepResult) { //do nothing
    }

    fun beforeStepUpdate(result: StepResult) { //do nothing
    }

    fun afterStepUpdate(result: StepResult) { //do nothing
    }

    fun beforeStepStop(result: StepResult) { //do nothing
    }

    fun afterStepStop(result: StepResult) { //do nothing
    }
}