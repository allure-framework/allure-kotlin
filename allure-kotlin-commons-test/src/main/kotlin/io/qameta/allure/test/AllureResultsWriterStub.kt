
package io.qameta.allure.test

import io.qameta.allure.AllureResultsWriter
import io.qameta.allure.model.TestResult
import io.qameta.allure.model.TestResultContainer
import io.qameta.allure.util.toByteArray
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class AllureResultsWriterStub : AllureResultsWriter, AllureResults {
    private val _testResults: MutableList<TestResult> = CopyOnWriteArrayList()
    private val _testContainers: MutableList<TestResultContainer> = CopyOnWriteArrayList()
    private val _attachments: MutableMap<String, ByteArray> = ConcurrentHashMap()

    override fun write(testResult: TestResult) {
        _testResults.add(testResult)
    }

    override fun write(testResultContainer: TestResultContainer) {
        _testContainers.add(testResultContainer)
    }

    override fun write(source: String, attachment: InputStream) {
        try {
            val bytes = attachment.toByteArray()
            _attachments[source] = bytes
        } catch (e: IOException) {
            throw RuntimeException("Could not read attachment content $source", e)
        }
    }

    override val testResults: List<TestResult>
        get() = _testResults
    override val testResultContainers: List<TestResultContainer>
        get() = _testContainers
    override val attachments: Map<String, ByteArray>
        get() = _attachments


}