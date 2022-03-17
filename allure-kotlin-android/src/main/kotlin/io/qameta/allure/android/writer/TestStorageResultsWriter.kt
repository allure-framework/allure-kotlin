package io.qameta.allure.android.writer

import androidx.test.services.storage.TestStorage
import io.qameta.allure.kotlin.AllureResultsWriter
import io.qameta.allure.kotlin.OutputStreamResultsWriter
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer
import io.qameta.allure.kotlin.util.PropertiesUtils
import java.io.InputStream

class TestStorageResultsWriter : AllureResultsWriter {
    private val defaultAllurePath by lazy { PropertiesUtils.resultsDirectoryPath }
    private val testStorage by lazy { TestStorage() }

    private val outputStreamResultsWriter = OutputStreamResultsWriter { name ->
        testStorage.openOutputFile("$defaultAllurePath/$name")
    }

    override fun write(testResult: TestResult) {
        outputStreamResultsWriter.write(testResult)
    }

    override fun write(testResultContainer: TestResultContainer) {
        outputStreamResultsWriter.write(testResultContainer)
    }

    override fun write(source: String, attachment: InputStream) {
        outputStreamResultsWriter.write(source, attachment)
    }
}