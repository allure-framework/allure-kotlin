package io.qameta.allure.kotlin.test

import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.AllureLifecycle
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.util.ResultsUtils.getStatus
import io.qameta.allure.kotlin.util.ResultsUtils.getStatusDetails
import java.util.*

object RunUtils {
    fun runWithinTestContext(runnable: Runnable): AllureResults {
        return runWithinTestContext(runnable, { Allure.lifecycle = it })
    }

    @JvmStatic
    @SafeVarargs
    fun runWithinTestContext(
        runnable: Runnable,
        vararg configurers: (AllureLifecycle) -> Unit
    ): AllureResults {
        val writer = AllureResultsWriterStub()
        val lifecycle = AllureLifecycle(writer)
        val uuid = UUID.randomUUID().toString()
        val result = TestResult(uuid)
        val cached = Allure.lifecycle
        try {
            configurers.forEach { it(lifecycle) }
            lifecycle.scheduleTestCase(result)
            lifecycle.startTestCase(uuid)
            runnable.run()
        } catch (e: Throwable) {
            lifecycle.updateTestCase(uuid) { testResult: TestResult ->
                getStatus(e)?.let { testResult.status = it }
                getStatusDetails(e)?.let { testResult.statusDetails = it }
            }
        } finally {
            lifecycle.stopTestCase(uuid)
            lifecycle.writeTestCase(uuid)
            configurers.forEach { it(cached) }
        }
        return writer
    }

}