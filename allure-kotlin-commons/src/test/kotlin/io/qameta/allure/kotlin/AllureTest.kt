package io.qameta.allure.kotlin

import io.github.benas.randombeans.api.EnhancedRandom
import io.qameta.allure.kotlin.Allure.attachment
import io.qameta.allure.kotlin.Allure.epic
import io.qameta.allure.kotlin.Allure.feature
import io.qameta.allure.kotlin.Allure.issue
import io.qameta.allure.kotlin.Allure.label
import io.qameta.allure.kotlin.Allure.lifecycle
import io.qameta.allure.kotlin.Allure.link
import io.qameta.allure.kotlin.Allure.parameter
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Allure.tms
import io.qameta.allure.kotlin.model.*
import io.qameta.allure.kotlin.model.Link
import io.qameta.allure.kotlin.test.RunUtils.runWithinTestContext
import io.qameta.allure.kotlin.test.function
import io.qameta.allure.kotlin.util.ObjectUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class AllureTest {

    private fun setLifecycle(lifecycle: AllureLifecycle) {
        Allure.lifecycle = lifecycle
    }

    @Test
    fun shouldAddSteps() {
        val results = runWithinTestContext(
            Runnable {
                step("first", Status.PASSED)
                step("second", Status.PASSED)
                step("third", Status.FAILED)
            },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .flatExtracting<StepResult, RuntimeException>(TestResult::steps)
            .extracting(
                function(StepResult::name),
                function(StepResult::status)
            )
            .containsExactly(
                Assertions.tuple("first", Status.PASSED),
                Assertions.tuple("second", Status.PASSED),
                Assertions.tuple("third", Status.FAILED)
            )
    }

    @Test
    fun shouldCreateStepsFromLambdas() {
        val results = runWithinTestContext(
            Runnable {
                step("first") {}
                step("second") { doSomething() }
                step("third") { Assertions.fail<Unit>("this step is failed") }
            },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .flatExtracting<StepResult, RuntimeException>(TestResult::steps)
            .extracting(
                function(StepResult::name),
                function(StepResult::status)
            )
            .containsExactly(
                Assertions.tuple("first", Status.PASSED),
                Assertions.tuple("second", Status.PASSED),
                Assertions.tuple("third", Status.FAILED)
            )
    }

    fun doSomething() {}

    @Test
    fun shouldHideCheckedExceptions() {
        val results = runWithinTestContext(
            Runnable {
                step("first", Status.PASSED)
                step("second") { throw Exception("something wrong") }
                step("third", Status.FAILED)
            },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .extracting<Status, RuntimeException>(TestResult::status)
            .containsExactly(Status.BROKEN)
        Assertions.assertThat(results.testResults)
            .extracting<StatusDetails, RuntimeException>(TestResult::statusDetails)
            .extracting<String, RuntimeException>(StatusDetails::message)
            .containsExactly("something wrong")
        Assertions.assertThat(results.testResults)
            .flatExtracting<StepResult, RuntimeException>(TestResult::steps)
            .extracting(
                function(StepResult::name),
                function(StepResult::status)
            )
            .containsExactly(
                Assertions.tuple("first", Status.PASSED),
                Assertions.tuple("second", Status.BROKEN)
            )
        Assertions.assertThat(results.testResults.flatMap { it.steps }
            .filter { it.name == "second" }
            .mapNotNull { it.statusDetails?.message })
            .containsExactly("something wrong")
    }

    @Test
    fun shouldAddLabels() {
        val first = EnhancedRandom.random(Label::class.java)
        val second = EnhancedRandom.random(Label::class.java)
        val third = EnhancedRandom.random(Label::class.java)
        val results = runWithinTestContext(
            Runnable {
                lifecycle.updateTestCase {
                    it.labels.addAll(listOf(first, second, third))
                }
            },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .flatExtracting<Label, RuntimeException>(TestResult::labels)
            .contains(third, first, second)
    }

    @Test
    fun shouldAddParameter() {
        val (name, value) = EnhancedRandom.random(Parameter::class.java)
        val (name1, value1) = EnhancedRandom.random(Parameter::class.java)
        val (name2, value2) = EnhancedRandom.random(Parameter::class.java)
        val results = runWithinTestContext(
            Runnable {
                parameter(name!!, value)
                parameter(name1!!, value1)
                parameter(name2!!, value2)
            },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .flatExtracting<Parameter, RuntimeException>(TestResult::parameters)
            .extracting(
                function(Parameter::name),
                function(Parameter::value)
            )
            .contains(
                Assertions.tuple(name, value),
                Assertions.tuple(name1, value1),
                Assertions.tuple(name2, value2)
            )
    }

    @Test
    fun shouldAddLinks() {
        val (name, url, type) = EnhancedRandom.random(Link::class.java)
        val (name1, url1) = EnhancedRandom.random(Link::class.java)
        val (_, url2) = EnhancedRandom.random(Link::class.java)
        val results = runWithinTestContext(
            Runnable {
                link(url = url!!, name = name!!, type = type!!)
                link(url = url1!!, name = name1!!)
                link(url = url2!!)
            },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .flatExtracting<Link, RuntimeException>(TestResult::links)
            .extracting(
                function(Link::name),
                function(Link::type),
                function(Link::url)
            )
            .contains(
                Assertions.tuple(name, type, url),
                Assertions.tuple(name1, null, url1),
                Assertions.tuple(null, null, url2)
            )
    }

    @Test
    fun shouldAddDescription() {
        val description = EnhancedRandom.random(String::class.java)
        val results = runWithinTestContext(
            Runnable { Allure.description(description) },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .extracting<String, RuntimeException>(TestResult::description)
            .containsExactly(description)
    }

    @Test
    fun shouldAddDescriptionHtml() {
        val descriptionHtml = EnhancedRandom.random(String::class.java)
        val results = runWithinTestContext(
            Runnable { Allure.descriptionHtml(descriptionHtml) },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .extracting<String, RuntimeException>(TestResult::descriptionHtml)
            .containsExactly(descriptionHtml)
    }

    @Test
    fun shouldSupportNewJavaApi() {
        val results = runWithinTestContext(
            Runnable { this.simpleTest() },
            ::setLifecycle
        )
        Assertions.assertThat(results.testResults)
            .hasSize(1)
        Assertions.assertThat(results.testResults)
            .flatExtracting<StepResult, RuntimeException>(TestResult::steps)
            .extracting(
                function(StepResult::name),
                function(StepResult::status)
            )
            .containsExactly(
                Assertions.tuple("set up test database", Status.SKIPPED),
                Assertions.tuple("set up test create mocks", Status.PASSED),
                Assertions.tuple("authorization", Status.PASSED),
                Assertions.tuple("preparation checks", Status.PASSED),
                Assertions.tuple("dynamic name ABC", Status.PASSED),
                Assertions.tuple("get data", Status.PASSED)
            )
        Assertions.assertThat(results.testResults
            .flatMap { it.steps }
            .filter { it.name == "get data" }
            .flatMap { it.steps })
            .extracting(
                function(StepResult::name),
                function(StepResult::status)
            )
            .containsExactly(
                Assertions.tuple("build client", Status.PASSED),
                Assertions.tuple("run request", Status.PASSED)
            )
        Assertions.assertThat(results.testResults
            .flatMap { it.steps }
            .filter { it.name == "get data" }
            .flatMap { it.steps }
            .filter { it.name == "run request" }
            .flatMap { it.parameters })
            .extracting(
                function(Parameter::name),
                function(Parameter::value)
            )
            .containsExactly(
                Assertions.tuple("authorization", "Basic admin:admin"),
                Assertions.tuple("url", "https://example.com/getData"),
                Assertions.tuple("requestBody", "[1, 2, 3]")
            )
    }

    fun simpleTest() {
        // Add test links
        // Add test links
        link("testing", "https://example.com")
        issue("GH-123", "https://github.com/allure-framework/allure2/issues/123")
        tms("AS-182", "https://allureee.qameta.io/project/1/test-cases/182")

        // Add test labels
        // Add test labels
        epic("Allure Java API")
        feature("Dynamic API")
        label("component", "allure-java-commons")

        // Add parameters to test within test body
        // Add parameters to test within test body
        val baseUrl = parameter("baseUrl", "https://example.com/getData")

        // Log-style steps
        // Log-style steps
        step("set up test database", Status.SKIPPED)
        step("set up test create mocks") // Status.PASSED by default

        // Add parameters to test inside steps as well
        val token = step("authorization") {
            val login = parameter("login", "admin")
            val password = parameter("password", "admin")
            getAuth(login, password)
        }

        // Add parameters to step using injected StepContext
        step("preparation checks") {
            parameter("a", "a value")
            parameter("b", "b value")
        }

        // Nested step and dynamic step name
        step {
            val a = step("child 1") { "A" }
            val b = step("child b") { "B" }
            val c = step("child b") { "C" }

            name("dynamic name $a$b$c")
        }

        // Create attachments as well as steps
        step("get data") {
            step("build client")
            val responseData = step("run request") {
                parameter("authorization", token)
                parameter("url", baseUrl)
                val requestBody = parameter("requestBody", intArrayOf(1, 2, 3))

                getData(baseUrl, token, requestBody)
            }
            attachment("response", ObjectUtils.toString(responseData))
        }

    }


    private fun getData(url: String, token: String, body: Any): List<String?> {
        return emptyList<String>()
    }

    private fun getAuth(login: String?, password: String?): String {
        return String.format("Basic %s:%s", login, password)
    }
}