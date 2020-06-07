package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.Allure.attachment
import io.qameta.allure.kotlin.listener.LifecycleNotifier
import io.qameta.allure.kotlin.listener.StepLifecycleListener
import io.qameta.allure.kotlin.model.Status
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.test.AllureResults
import io.qameta.allure.kotlin.test.AllureResultsWriterStub
import io.qameta.allure.kotlin.util.ResultsUtils.getStatus
import io.qameta.allure.kotlin.util.ResultsUtils.getStatusDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class StepLifecycleListenerTest {
    @Test
    fun shouldExecuteBeforeStepStart() {
        val executionCount = AtomicInteger()
        val listener: StepLifecycleListener = object : StepLifecycleListener {
            override fun beforeStepStart(result: StepResult) {
                executionCount.incrementAndGet()
            }
        }
        val run = run(listener, "first", "second")
        assertThat(run.testResults
            .flatMap { it.steps }
            .map { it.name })
            .containsExactly("first", "second")
        assertThat(executionCount.get())
            .isEqualTo(2)
    }

    @Test
    fun shouldExecuteAfterStepStart() {
        val executionCount = AtomicInteger()
        val listener: StepLifecycleListener = object : StepLifecycleListener {
            override fun afterStepStart(result: StepResult) {
                executionCount.incrementAndGet()
                attachment("inner " + result.name, "some")
            }
        }
        val run = run(listener, "first", "second")
        assertThat(run.testResults
            .flatMap { it.steps }
            .map { it.name })
            .containsExactly("first", "second")
        assertThat(run.testResults
            .flatMap { it.steps }
            .filter { it.name == "first" }
            .flatMap { it.attachments }
            .map { it.name })
            .containsExactly("inner first")
        assertThat(run.testResults
            .flatMap { it.steps }
            .filter { it.name == "second" }
            .flatMap { it.attachments }
            .map { it.name })
            .containsExactly("inner second")
        assertThat(executionCount.get())
            .isEqualTo(2)
    }

    @Issue("177")
    @Test
    fun shouldExecuteBeforeStepStop() {
        val executionCount = AtomicInteger()
        val listener: StepLifecycleListener = object : StepLifecycleListener {
            override fun beforeStepStop(result: StepResult) {
                executionCount.incrementAndGet()
                attachment("inner " + result.name, "some")
            }
        }
        val run = run(listener, "first", "second")
        assertThat(run.testResults
            .flatMap { it.steps }
            .map { it.name })
            .containsExactly("first", "second")
        assertThat(run.testResults
            .flatMap { it.steps }
            .filter { it.name == "first" }
            .flatMap { it.attachments }
            .map { it.name })
            .containsExactly("inner first")
        assertThat(run.testResults
            .flatMap { it.steps }
            .filter { it.name == "second" }
            .flatMap { it.attachments }
            .map { it.name })
            .containsExactly("inner second")
        assertThat(executionCount.get())
            .isEqualTo(2)
    }

    protected fun run(listener: StepLifecycleListener, vararg steps: String): AllureResults {
        val writer = AllureResultsWriterStub()
        val notifier = LifecycleNotifier(
            emptyList(),
            emptyList(),
            emptyList(),
            listOf(listener)
        )
        val lifecycle = AllureLifecycle(writer, notifier)
        val uuid = UUID.randomUUID().toString()
        val result = TestResult(uuid)
        val cached = Allure.lifecycle
        try {
            Allure.lifecycle = lifecycle
            lifecycle.scheduleTestCase(result)
            lifecycle.startTestCase(uuid)
            steps
                .forEach { step: String ->
                    val stepUuid = UUID.randomUUID().toString()
                    lifecycle.startStep(
                        stepUuid,
                        StepResult().apply {
                            this.name = step
                            this.status = Status.PASSED
                        }
                    )
                    lifecycle.stopStep(stepUuid)
                }
        } catch (e: Throwable) {
            lifecycle.updateTestCase(uuid) { testResult: TestResult ->
                getStatus(e)?.let { testResult.status = it }
                getStatusDetails(e)?.let { testResult.statusDetails = it }
            }
        } finally {
            lifecycle.stopTestCase(uuid)
            lifecycle.writeTestCase(uuid)
            Allure.lifecycle = cached
        }
        return writer
    }
}