package io.qameta.allure.kotlin.listener

import io.qameta.allure.kotlin.model.TestResultContainer

/**
 * Notifies about Allure test container lifecycle.
 *
 * @since 2.0
 */
interface ContainerLifecycleListener : LifecycleListener {
    fun beforeContainerStart(container: TestResultContainer) { //do nothing
    }

    fun afterContainerStart(container: TestResultContainer) { //do nothing
    }

    fun beforeContainerUpdate(container: TestResultContainer) { //do nothing
    }

    fun afterContainerUpdate(container: TestResultContainer) { //do nothing
    }

    fun beforeContainerStop(container: TestResultContainer) { //do nothing
    }

    fun afterContainerStop(container: TestResultContainer) { //do nothing
    }

    fun beforeContainerWrite(container: TestResultContainer) { //do nothing
    }

    fun afterContainerWrite(container: TestResultContainer) { //do nothing
    }
}