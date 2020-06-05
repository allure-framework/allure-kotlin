package io.qameta.allure.kotlin.listener

import io.qameta.allure.kotlin.model.FixtureResult
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer
import io.qameta.allure.kotlin.util.error
import io.qameta.allure.kotlin.util.loggerFor
import java.util.logging.Logger

/**
 * @since 2.0
 */
class LifecycleNotifier(
    private val containerListeners: List<ContainerLifecycleListener>,
    private val testListeners: List<TestLifecycleListener>,
    private val fixtureListeners: List<FixtureLifecycleListener>,
    private val stepListeners: List<StepLifecycleListener>
) : ContainerLifecycleListener, TestLifecycleListener, FixtureLifecycleListener, StepLifecycleListener {
    override fun beforeTestSchedule(result: TestResult) {
        testListeners.runSafely { it.beforeTestSchedule(result) }
    }

    override fun afterTestSchedule(result: TestResult) {
        testListeners.runSafely { it.afterTestSchedule(result) }
    }

    override fun beforeTestUpdate(result: TestResult) {
        testListeners.runSafely { it.beforeTestUpdate(result) }
    }

    override fun afterTestUpdate(result: TestResult) {
        testListeners.runSafely { it.afterTestUpdate(result) }
    }

    override fun beforeTestStart(result: TestResult) {
        testListeners.runSafely { it.beforeTestStart(result) }
    }

    override fun afterTestStart(result: TestResult) {
        testListeners.runSafely { it.afterTestStart(result) }
    }

    override fun beforeTestStop(result: TestResult) {
        testListeners.runSafely { it.beforeTestStop(result) }
    }

    override fun afterTestStop(result: TestResult) {
        testListeners.runSafely { it.afterTestStop(result) }
    }

    override fun beforeTestWrite(result: TestResult) {
        testListeners.runSafely { it.beforeTestWrite(result) }
    }

    override fun afterTestWrite(result: TestResult) {
        testListeners.runSafely { it.afterTestWrite(result) }
    }

    override fun beforeContainerStart(container: TestResultContainer) {
        containerListeners.runSafely { it.beforeContainerStart(container) }
    }

    override fun afterContainerStart(container: TestResultContainer) {
        containerListeners.runSafely { it.afterContainerStart(container) }
    }

    override fun beforeContainerUpdate(container: TestResultContainer) {
        containerListeners.runSafely { it.beforeContainerUpdate(container) }
    }

    override fun afterContainerUpdate(container: TestResultContainer) {
        containerListeners.runSafely { it.afterContainerUpdate(container) }
    }

    override fun beforeContainerStop(container: TestResultContainer) {
        containerListeners.runSafely { it.beforeContainerStop(container) }
    }

    override fun afterContainerStop(container: TestResultContainer) {
        containerListeners.runSafely { it.afterContainerStop(container) }
    }

    override fun beforeContainerWrite(container: TestResultContainer) {
        containerListeners.runSafely { it.beforeContainerWrite(container) }
    }

    override fun afterContainerWrite(container: TestResultContainer) {
        containerListeners.runSafely { it.afterContainerWrite(container) }
    }

    override fun beforeFixtureStart(result: FixtureResult) {
        fixtureListeners.runSafely { it.beforeFixtureStart(result) }
    }

    override fun afterFixtureStart(result: FixtureResult) {
        fixtureListeners.runSafely { it.afterFixtureStart(result) }
    }

    override fun beforeFixtureUpdate(result: FixtureResult) {
        fixtureListeners.runSafely { it.beforeFixtureUpdate(result) }
    }

    override fun afterFixtureUpdate(result: FixtureResult) {
        fixtureListeners.runSafely { it.afterFixtureUpdate(result) }
    }

    override fun beforeFixtureStop(result: FixtureResult) {
        fixtureListeners.runSafely { it.beforeFixtureStop(result) }
    }

    override fun afterFixtureStop(result: FixtureResult) {
        fixtureListeners.runSafely { it.afterFixtureStop(result) }
    }

    override fun beforeStepStart(result: StepResult) {
        stepListeners.runSafely { it.beforeStepStart(result) }
    }

    override fun afterStepStart(result: StepResult) {
        stepListeners.runSafely { it.afterStepStart(result) }
    }

    override fun beforeStepUpdate(result: StepResult) {
        stepListeners.runSafely { it.beforeStepUpdate(result) }
    }

    override fun afterStepUpdate(result: StepResult) {
        stepListeners.runSafely { it.afterStepUpdate(result) }
    }

    override fun beforeStepStop(result: StepResult) {
        stepListeners.runSafely { it.beforeStepStop(result) }
    }

    override fun afterStepStop(result: StepResult) {
        stepListeners.runSafely { it.afterStepStop(result) }
    }

    private fun <T : LifecycleListener> List<T>.runSafely(method: (T) -> Unit) {
        forEach { listener ->
            try {
                method(listener)
            } catch (e: Exception) {
                LOGGER.error("Could not invoke listener method", e)
            }
        }
    }

    companion object {
        private val LOGGER: Logger = loggerFor<LifecycleNotifier>()
    }
}