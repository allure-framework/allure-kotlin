package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class OutputStreamResultsWriter(private val streamProvider: (name: String) -> OutputStream) : AllureResultsWriter {

    private val mapper: Json = Json {
        prettyPrint = true
        useArrayPolymorphism = true
    }

    override fun write(testResult: TestResult) {
        val testResultName = generateTestResultName(testResult.uuid)
        try {
            val json = mapper.encodeToString(TestResult.serializer(), testResult)
            streamProvider(testResultName).use {
                it.write(json.toByteArray())
            }
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not write Allure test result", e)
        }
    }

    override fun write(testResultContainer: TestResultContainer) {
        val testResultContainerName = generateTestResultContainerName(testResultContainer.uuid)
        try {
            val json = mapper.encodeToString(TestResultContainer.serializer(), testResultContainer)
            streamProvider(testResultContainerName).use {
                it.write(json.toByteArray())
            }
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not write Allure test result container", e)
        }
    }

    override fun write(source: String, attachment: InputStream) {
        try {
            attachment.use { input ->
                streamProvider(source).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not write Allure attachment", e)
        }
    }

    private fun generateTestResultContainerName(uuid: String? = UUID.randomUUID().toString()): String =
        uuid + AllureConstants.TEST_RESULT_CONTAINER_FILE_SUFFIX

    companion object {
        @JvmStatic
        @JvmOverloads
        fun generateTestResultName(uuid: String = UUID.randomUUID().toString()): String {
            return uuid + AllureConstants.TEST_RESULT_FILE_SUFFIX
        }

    }
}