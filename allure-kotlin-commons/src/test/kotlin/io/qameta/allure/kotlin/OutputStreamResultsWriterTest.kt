package io.qameta.allure.kotlin

import io.github.benas.randombeans.api.EnhancedRandom
import io.qameta.allure.kotlin.FileSystemResultsWriter.Companion.generateTestResultName
import io.qameta.allure.kotlin.model.*
import io.qameta.allure.kotlin.model.Attachment
import io.qameta.allure.kotlin.model.Link
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class OutputStreamResultsWriterTest {

    @Test
    fun shouldWriteTestResult() {
        var name = ""
        val os = ByteArrayOutputStream()
        val writer = OutputStreamResultsWriter {
            name = it
            os
        }
        val uuid = UUID.randomUUID().toString()
        val testResult = generateTestResult(uuid)
        writer.write(testResult)

        val expectedName = generateTestResultName(uuid)
        Assertions.assertThat(name)
            .isEqualTo(expectedName)
        Assertions.assertThat(os.size())
            .isGreaterThan(0)
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