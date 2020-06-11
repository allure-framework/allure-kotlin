package io.qameta.allure.junit4

import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.model.*
import io.qameta.allure.util.AnnotationUtils
import io.qameta.allure.util.AnnotationUtils.getLabels
import io.qameta.allure.util.AnnotationUtils.getLinks
import io.qameta.allure.util.ResultsUtils
import io.qameta.allure.util.ResultsUtils.createFrameworkLabel
import io.qameta.allure.util.ResultsUtils.createHostLabel
import io.qameta.allure.util.ResultsUtils.createLanguageLabel
import io.qameta.allure.util.ResultsUtils.createPackageLabel
import io.qameta.allure.util.ResultsUtils.createSuiteLabel
import io.qameta.allure.util.ResultsUtils.createTestClassLabel
import io.qameta.allure.util.ResultsUtils.createTestMethodLabel
import io.qameta.allure.util.ResultsUtils.createThreadLabel
import io.qameta.allure.util.ResultsUtils.getStatus
import io.qameta.allure.util.ResultsUtils.getStatusDetails
import io.qameta.allure.util.ResultsUtils.md5
import org.junit.Ignore
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import java.util.*

/**
 * Allure Junit4 listener.
 */
@RunListener.ThreadSafe
class AllureJunit4 @JvmOverloads constructor(val lifecycle: AllureLifecycle = Allure.lifecycle) :
    RunListener() {
    private val testCases: ThreadLocal<String> = object : InheritableThreadLocal<String>() {
        override fun initialValue(): String = UUID.randomUUID().toString()
    }

    override fun testRunStarted(description: Description) { //do nothing
    }

    override fun testRunFinished(result: Result) { //do nothing
    }

    override fun testStarted(description: Description) {
        val uuid = testCases.get()
        val result = createTestResult(uuid, description)
        lifecycle.scheduleTestCase(result)
        lifecycle.startTestCase(uuid)
    }

    override fun testFinished(description: Description) {
        val uuid = testCases.get()
        testCases.remove()
        lifecycle.updateTestCase(uuid) { testResult: TestResult ->
            if (testResult.status == null) {
                testResult.status = Status.PASSED
            }
        }
        lifecycle.stopTestCase(uuid)
        lifecycle.writeTestCase(uuid)
    }

    override fun testFailure(failure: Failure) {
        val uuid = testCases.get()
        lifecycle.updateTestCase(uuid) { testResult: TestResult ->
            with(testResult) {
                status = getStatus(failure.exception)
                statusDetails = getStatusDetails(failure.exception)
            }
        }
    }

    override fun testAssumptionFailure(failure: Failure) {
        val uuid = testCases.get()
        lifecycle.updateTestCase(uuid) { testResult: TestResult ->
            with(testResult) {
                status = Status.SKIPPED
                statusDetails = getStatusDetails(failure.exception)
            }
        }
    }

    override fun testIgnored(description: Description) {
        val uuid = testCases.get()
        testCases.remove()

        val result = createTestResult(uuid, description).apply {
            status = Status.SKIPPED
            statusDetails = getIgnoredMessage(description)
            start = System.currentTimeMillis()
        }
        lifecycle.scheduleTestCase(result)
        lifecycle.stopTestCase(uuid)
        lifecycle.writeTestCase(uuid)
    }

    private fun getDisplayName(result: Description): String? {
        return result.getAnnotation(DisplayName::class.java)?.value
    }

    private fun getDescription(result: Description): String? {
        return result.getAnnotation(io.qameta.allure.Description::class.java)?.value
    }

    private fun extractLinks(description: Description): List<Link> {
        val result = ArrayList(getLinks(description.annotations))
        description.testClass
            ?.let(AnnotationUtils::getLinks)
            ?.let(result::addAll)
        return result
    }

    private fun extractLabels(description: Description): List<Label> {
        val result = ArrayList(getLabels(description.annotations))
        description.testClass
            ?.let(AnnotationUtils::getLabels)
            ?.let(result::addAll)
        return result
    }

    private fun getHistoryId(description: Description): String {
        return md5(description.className + description.methodName)
    }

    private fun getPackage(testClass: Class<*>?): String {
        return testClass?.getPackage()?.name ?: ""
    }

    private fun getIgnoredMessage(description: Description): StatusDetails {
        val ignore: Ignore? = description.getAnnotation(Ignore::class.java)
        val message = ignore?.value?.takeIf { it.isNotEmpty() } ?: "Test ignored (without reason)!"
        return StatusDetails(message = message)
    }

    private fun createTestResult(uuid: String, description: Description): TestResult {
        val className: String = description.className
        val methodName: String? = description.methodName
        val name = methodName ?: className
        val fullName = if (methodName != null) "$className.$methodName" else className
        val suite: String = description.testClass?.getAnnotation(DisplayName::class.java)?.value ?: className
        val testResult: TestResult = TestResult(uuid).apply {
            this.historyId = getHistoryId(description)
            this.fullName = fullName
            this.name = name
        }
        testResult.labels.addAll(ResultsUtils.getProvidedLabels())
        testResult.labels.addAll(
            listOf(
                createPackageLabel(getPackage(description.testClass)),
                createTestClassLabel(className),
                createTestMethodLabel(name),
                createSuiteLabel(suite),
                createHostLabel(),
                createThreadLabel(),
                createFrameworkLabel("junit4"),
                createLanguageLabel("kotlin")
            )
        )
        testResult.labels.addAll(extractLabels(description))
        testResult.links.addAll(extractLinks(description))
        getDisplayName(description)?.let {
            testResult.name = it
        }
        getDescription(description)?.let {
            testResult.description = it
        }
        return testResult
    }

}