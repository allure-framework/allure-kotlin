package io.qameta.allure.kotlin

import io.github.benas.randombeans.api.EnhancedRandom
import io.qameta.allure.kotlin.FileSystemResultsWriter.Companion.generateTestResultName
import io.qameta.allure.kotlin.model.*
import io.qameta.allure.kotlin.model.Attachment
import io.qameta.allure.kotlin.model.Link
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*

class FileSystemResultsWriterTest {
    @Test
    fun shouldNotFailIfNoResultsDirectory(@TempDir folder: File) {
        val resolve = folder.resolve("some-directory")
        val writer = FileSystemResultsWriter(resolve)
        val testResult = generateTestResult()
        writer.write(testResult)
    }

    @Test
    fun shouldWriteTestResult(@TempDir folder: File) {
        val writer = FileSystemResultsWriter(folder)
        val uuid = UUID.randomUUID().toString()
        val testResult = generateTestResult(uuid)
        writer.write(testResult)
        val fileName = generateTestResultName(uuid)
        Assertions.assertThat(folder)
            .isDirectory()
        Assertions.assertThat(folder.resolve(fileName))
            .isFile()
    }

    private fun generateTestResult(uuid: String = EnhancedRandom.random(String::class.java)): TestResult = TestResult(
        uuid = uuid,
        historyId = uuid,
        testCaseId = uuid,
        rerunOf = uuid,
        fullName = uuid,
        labels = EnhancedRandom.randomListOf(10, Label::class.java),
        links = EnhancedRandom.randomListOf(10, Link::class.java)
    ).apply {
        name = uuid
        start = EnhancedRandom.random(Long::class.java)
        stop = EnhancedRandom.random(Long::class.java)
        stage = EnhancedRandom.random(Stage::class.java)
        description = uuid
        descriptionHtml = uuid
        status = EnhancedRandom.random(Status::class.java)
        statusDetails = EnhancedRandom.random(StatusDetails::class.java)
        steps.addAll(EnhancedRandom.randomListOf(10, StepResult::class.java))
        attachments.addAll(EnhancedRandom.randomListOf(10, Attachment::class.java))
        parameters.addAll(EnhancedRandom.randomListOf(10, Parameter::class.java))
    }
}