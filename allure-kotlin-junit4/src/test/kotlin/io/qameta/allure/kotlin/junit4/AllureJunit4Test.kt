package io.qameta.allure.kotlin.junit4

import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.AllureLifecycle
import io.qameta.allure.kotlin.Step
import io.qameta.allure.kotlin.junit4.samples.*
import io.qameta.allure.kotlin.junit4.samples.IgnoredTests
import io.qameta.allure.kotlin.model.Stage
import io.qameta.allure.kotlin.model.Status
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.test.AllureFeatures
import io.qameta.allure.kotlin.test.AllureFeatures.*
import io.qameta.allure.kotlin.test.AllureResults
import io.qameta.allure.kotlin.test.AllureResultsWriterStub
import io.qameta.allure.kotlin.test.function
import io.qameta.allure.kotlin.util.ResultsUtils.HOST_LABEL_NAME
import io.qameta.allure.kotlin.util.ResultsUtils.THREAD_LABEL_NAME
import org.assertj.core.api.Assertions
import org.assertj.core.api.iterable.Extractor
import org.junit.jupiter.api.Test
import org.junit.runner.JUnitCore

class AllureJunit4Test {
    @Test
    @FullName
    fun shouldSetTestFullName() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::fullName)
            .containsExactly("io.qameta.allure.kotlin.junit4.samples.OneTest.simpleTest")
    }

    @Test
    @Timeline
    fun shouldSetExecutionLabels() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults
            .flatMap { it.labels }
            .map { it.name })
            .contains(HOST_LABEL_NAME, THREAD_LABEL_NAME)
    }

    @Test
    @Timings
    fun shouldSetTestStart() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::start)
            .isNotNull()
    }

    @Test
    @Timings
    fun shouldSetTestStop() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::stop)
            .isNotNull()
    }

    @Test
    @Stages
    fun shouldSetStageFinished() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::stage)
            .containsExactly(Stage.FINISHED)
    }

    @Test
    @PassedTests
    fun shouldSetPassedStatus() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::status)
            .containsExactly(Status.PASSED)
    }

    @Test
    @FailedTests
    fun shouldProcessFailedTest() {
        val results = runClasses(FailedTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::status)
            .containsExactly(Status.FAILED)
    }

    @Test
    @BrokenTests
    fun shouldProcessBrokenTest() {
        val results = runClasses(BrokenTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::status)
            .containsExactly(Status.BROKEN)
        Assertions.assertThat(testResults[0].statusDetails)
            .hasFieldOrPropertyWithValue("message", "Hello, everybody")
            .hasFieldOrProperty("trace")
    }

    @Test
    @BrokenTests
    fun shouldProcessBrokenWithoutMessageTest() {
        val results = runClasses(BrokenWithoutMessageTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::status)
            .containsExactly(Status.BROKEN)
        Assertions.assertThat(testResults[0].statusDetails)
            .hasFieldOrPropertyWithValue("message", "java.lang.RuntimeException")
            .hasFieldOrProperty("trace")
    }

    @Test
    @SkippedTests
    fun shouldProcessSkippedTest() {
        val results = runClasses(AssumptionFailedTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::status)
            .containsExactly(Status.SKIPPED)
    }

    @Test
    @AllureFeatures.IgnoredTests
    fun shouldProcessIgnoredTest() {
        val results = runClasses(IgnoredTests::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(2)
            .flatExtracting(extractor(TestResult::status))
            .containsExactly(Status.SKIPPED, Status.SKIPPED)
    }

    @Test
    @AllureFeatures.IgnoredTests
    fun shouldProcessIgnoredTestDescription() {
        val results = runClasses(IgnoredTests::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(2)
        Assertions.assertThat(testResults.map { it.statusDetails?.message })
            .containsExactlyInAnyOrder("Test ignored (without reason)!", "Ignored for some reason")
    }

    @Test
    @AllureFeatures.IgnoredTests
    @DisplayName("Test result for ignored class gets named by the class name")
    fun shouldSetNameForIgnoredClass() {
        val results = runClasses(IgnoredClassTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .extracting<Any, Exception>(TestResult::name)
            .containsExactly("io.qameta.allure.kotlin.junit4.samples.IgnoredClassTest")
    }

    @Test
    @Steps
    @DisplayName("Test with steps")
    fun shouldAddStepsToTest() {
        val results = runClasses(TestWithSteps::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
        Assertions.assertThat(testResults.flatMap { it.steps }.map { it.name })
            .hasSize(3)
            .containsExactly("step1", "step2", "step3")
    }

    @Test
    @Steps
    fun testWithTimeoutAndSteps() {
        val results = runClasses(TestWithTimeout::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)

        Assertions.assertThat(testResults.flatMap { it.steps })
            .hasSize(2)
            .extracting(extractor(StepResult::name))
            .containsExactly("Step 1", "Step 2")
    }

    @Test
    @MarkerAnnotations
    fun shouldProcessMethodAnnotations() {
        val results = runClasses(TestWithAnnotations::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
        Assertions.assertThat(testResults.flatMap { it.labels }.map { it.value })
            .contains(
                "epic1", "epic2", "epic3",
                "feature1", "feature2", "feature3",
                "story1", "story2", "story3",
                "some-owner"
            )
    }

    @Test
    @AllureFeatures.DisplayName
    fun shouldSetDisplayName() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::name)
            .containsExactly("Simple test")
    }

    @Test
    @Trees
    fun shouldSetSuiteName() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
        Assertions.assertThat(testResults
            .flatMap { it.labels }
            .filter { it.name == "suite" }
            .map { it.value })
            .containsExactly("Should be overwritten by method annotation")
    }

    @Test
    @Descriptions
    fun shouldSetDescription() {
        val results = runClasses(OneTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting<Any, Exception>(TestResult::description)
            .containsExactly("Description here")
    }

    @Test
    @Links
    fun shouldSetLinks() {
        val results = runClasses(FailedTest::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
        Assertions.assertThat(testResults
            .flatMap { it.links }
            .map { it.name })
            .containsExactlyInAnyOrder("link-1", "link-2", "issue-1", "issue-2", "tms-1", "tms-2")
    }

    @Test
    @MarkerAnnotations
    fun shouldSetTags() {
        val results = runClasses(TaggedTests::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
        Assertions.assertThat(testResults.flatMap { it.labels }
            .filter { it.name == "tag" }
            .map { it.value })
            .containsExactlyInAnyOrder(
                TaggedTests.CLASS_TAG1,
                TaggedTests.CLASS_TAG2,
                TaggedTests.METHOD_TAG1,
                TaggedTests.METHOD_TAG2
            )
    }

    @Test
    @Base
    @Throws(Exception::class)
    fun shouldProcessTestFromDefaultPackage() {
        val testInDefaultPackage = Class.forName("SampleTestInDefaultPackage")
        val results = runClasses(testInDefaultPackage)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting(function(TestResult::fullName), function(TestResult::status))
            .containsExactly(
                Assertions.tuple(
                    "SampleTestInDefaultPackage.testMethod",
                    Status.PASSED
                )
            )
    }

    @Test
    @Base
    fun shouldSupportTestsNotBasedOnClasses() {
        val results = runClasses(TestBasedOnSampleRunner::class.java)
        val testResults = results.testResults
        Assertions.assertThat(testResults)
            .hasSize(1)
            .extracting(function(TestResult::name), function(TestResult::status))
            .containsExactly(
                Assertions.tuple("Some human readable name", Status.PASSED)
            )
    }

    @Step("Run classes {classes}")
    private fun runClasses(vararg classes: Class<*>): AllureResults {
        val writerStub = AllureResultsWriterStub()
        val lifecycle = AllureLifecycle(writerStub)
        val core = JUnitCore()
        core.addListener(AllureJunit4(lifecycle))
        val defaultLifecycle = Allure.lifecycle
        return try {
            Allure.lifecycle = lifecycle
            core.run(*classes)
            writerStub
        } finally {
            Allure.lifecycle = defaultLifecycle
        }
    }
}

private fun <T, R> extractor(extraction: (T) -> R): Extractor<T, R> = Extractor { extraction(it) }