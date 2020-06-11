
package io.qameta.allure.test

import io.qameta.allure.model.TestResult
import io.qameta.allure.model.TestResultContainer

interface AllureResults {
    val testResults: List<TestResult>
    val testResultContainers: List<TestResultContainer>
    val attachments: Map<String, ByteArray>
}