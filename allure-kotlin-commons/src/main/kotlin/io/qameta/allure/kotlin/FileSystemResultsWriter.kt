package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class FileSystemResultsWriter(private val outputDirectory: File) : AllureResultsWriter {
    private val mapper: Json = Json.indented

    override fun write(testResult: TestResult) {
        val testResultName = generateTestResultName(testResult.uuid)
        createDirectories(outputDirectory)
        val file = outputDirectory.resolve(testResultName)
        try {
            val json = mapper.stringify(TestResult.serializer(), testResult)
            file.writeText(json)
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not write Allure test result", e)
        }
    }

    override fun write(testResultContainer: TestResultContainer) {
        val testResultContainerName = generateTestResultContainerName(testResultContainer.uuid)
        createDirectories(outputDirectory)
        val filePath = outputDirectory.resolve(testResultContainerName)
        try {
            val json = mapper.stringify(TestResultContainer.serializer(), testResultContainer)
            filePath.writeText(json)
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not write Allure test result container", e)
        }
    }

    override fun write(source: String, attachment: InputStream) {
        createDirectories(outputDirectory)
        val filePath = outputDirectory.resolve(source)
        try {
            attachment.use { input ->
                filePath.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not write Allure attachment", e)
        }
    }

    private fun createDirectories(directory: File) {
        val parent = directory.parentFile
        if (!parent.exists()) {
            createDirectories(parent)
        }
        if (!directory.exists() && !directory.mkdirs()) {
            throw AllureResultsWriteException("Could not create Allure results directory")
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