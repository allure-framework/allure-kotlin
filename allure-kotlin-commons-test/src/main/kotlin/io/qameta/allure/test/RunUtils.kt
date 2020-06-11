package io.qameta.allure.test

import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.model.TestResult
import io.qameta.allure.util.ResultsUtils.getStatus
import io.qameta.allure.util.ResultsUtils.getStatusDetails
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