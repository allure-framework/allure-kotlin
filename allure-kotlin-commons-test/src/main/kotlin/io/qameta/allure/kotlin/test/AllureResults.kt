
package io.qameta.allure.kotlin.test

import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer

interface AllureResults {
    val testResults: List<TestResult>
    val testResultContainers: List<TestResultContainer>
    val attachments: Map<String, ByteArray>
}