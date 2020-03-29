package io.qameta.allure.kotlin

import io.github.benas.randombeans.api.EnhancedRandom.random
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.qameta.allure.kotlin.model.FixtureResult
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer
import io.qameta.allure.kotlin.util.extractor
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class AllureLifecycleTest {
    private lateinit var writer: AllureResultsWriter
    private lateinit var lifecycle: AllureLifecycle
    @BeforeEach
    fun setUp() {
        this.writer = mockk(relaxed = true)
        this.lifecycle = AllureLifecycle(writer = writer)
    }

    @Test
    fun shouldCreateTest() {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val result = TestResult(uuid).apply { this.name = name }
        lifecycle.scheduleTestCase(result)
        lifecycle.startTestCase(uuid)
        lifecycle.stopTestCase(uuid)
        lifecycle.writeTestCase(uuid)
        val slot = slot<TestResult>()
        verify(exactly = 1) { writer.write(capture(slot)) }
        Assertions.assertThat(slot.captured)
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", uuid)
            .hasFieldOrPropertyWithValue("name", name)
    }

    @Test
    fun shouldCreateTestContainer() {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val container = TestResultContainer(uuid = uuid, name = name)
        lifecycle.startTestContainer(container)
        lifecycle.stopTestContainer(uuid)
        lifecycle.writeTestContainer(uuid)
        val slot = slot<TestResultContainer>()
        verify(exactly = 1) { writer.write(capture(slot)) }
        Assertions.assertThat(slot.captured)
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", uuid)
            .hasFieldOrPropertyWithValue("name", name)
    }

    @Test
    fun shouldCreateChildTestContainer() {
        val parentUuid = random(String::class.java)
        val parentName = random(String::class.java)
        val parent = TestResultContainer(uuid = parentUuid, name = parentName)
        lifecycle.startTestContainer(parent)
        val childUuid = random(String::class.java)
        val childName = random(String::class.java)
        val container = TestResultContainer(uuid = childUuid, name = childName)
        lifecycle.startTestContainer(parentUuid, container)
        lifecycle.stopTestContainer(childUuid)
        lifecycle.writeTestContainer(childUuid)
        lifecycle.stopTestContainer(parentUuid)
        lifecycle.writeTestContainer(parentUuid)
        val values = mutableListOf<TestResultContainer>()
        verify(exactly = 2) { writer.write(capture(values)) }
        Assertions.assertThat(values)
            .isNotNull
            .hasSize(2)
        Assertions.assertThat(values[0])
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", childUuid)
            .hasFieldOrPropertyWithValue("name", childName)
        val actual = values[1]
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", parentUuid)
            .hasFieldOrPropertyWithValue("name", parentName)
        Assertions.assertThat(actual.children)
            .containsExactly(childUuid)
    }

    @Test
    fun shouldAddStepsToTests() {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val result: TestResult = TestResult(uuid).apply { this.name = name }
        lifecycle.scheduleTestCase(result)
        lifecycle.startTestCase(uuid)
        val firstStepName = randomStep(uuid)
        val secondStepName = randomStep(uuid)
        lifecycle.stopTestCase(uuid)
        lifecycle.writeTestCase(uuid)
        val slot = slot<TestResult>()
        verify(exactly = 1) { writer.write(capture(slot)) }
        val actual = slot.captured
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", uuid)
            .hasFieldOrPropertyWithValue("name", name)
        Assertions.assertThat(actual.steps)
            .flatExtracting(extractor(StepResult::name))
            .containsExactly(firstStepName, secondStepName)
    }

    @Test
    fun shouldUpdateTest() {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val result: TestResult = TestResult(uuid).apply { this.name = name }
        lifecycle.scheduleTestCase(result)
        lifecycle.startTestCase(uuid)
        val stepUuid = random(String::class.java)
        val stepName = random(String::class.java)
        val step: StepResult = StepResult().apply {
            this.name = stepName
        }
        lifecycle.startStep(uuid, stepUuid, step)
        val description = random(String::class.java)
        val fullName = random(String::class.java)
        lifecycle.updateTestCase(
            uuid
        ) { testResult: TestResult -> testResult.description = description }
        lifecycle.updateTestCase { testResult: TestResult -> testResult.fullName = fullName }
        lifecycle.stopStep(stepUuid)
        lifecycle.stopTestCase(uuid)
        lifecycle.writeTestCase(uuid)
        val slot = slot<TestResult>()
        verify(exactly = 1) { writer.write(capture(slot)) }
        val actual = slot.captured
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", uuid)
            .hasFieldOrPropertyWithValue("description", description)
            .hasFieldOrPropertyWithValue("name", name)
            .hasFieldOrPropertyWithValue("fullName", fullName)
        Assertions.assertThat(actual.steps)
            .flatExtracting(extractor(StepResult::name))
            .containsExactly(stepName)
    }

    @Test
    fun shouldUpdateContainer() {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val newName = random(String::class.java)
        val container: TestResultContainer = TestResultContainer(uuid, name)
        lifecycle.startTestContainer(container)
        lifecycle.updateTestContainer(uuid) { c: TestResultContainer -> c.name = newName }
        lifecycle.stopTestContainer(uuid)
        lifecycle.writeTestContainer(uuid)
        val slot = slot<TestResultContainer>()
        verify(exactly = 1) { writer.write(capture(slot)) }
        Assertions.assertThat(slot.captured)
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", uuid)
            .hasFieldOrPropertyWithValue("name", newName)
    }

    @Test
    fun shouldCreateTestFixture() {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val container = TestResultContainer(uuid, name)
        lifecycle.startTestContainer(container)
        val firstUuid = random(String::class.java)
        val firstName = random(String::class.java)
        val first: FixtureResult = FixtureResult().apply {
            this.name = firstName
        }
        lifecycle.startPrepareFixture(uuid, firstUuid, first)
        val firstStepName = randomStep(firstUuid)
        val secondStepName = randomStep(firstUuid)
        lifecycle.stopFixture(firstUuid)
        lifecycle.stopTestContainer(uuid)
        lifecycle.writeTestContainer(uuid)
        val slot = slot<TestResultContainer>()
        verify(exactly = 1) { writer.write(capture(slot)) }
        val actual = slot.captured
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("uuid", uuid)
            .hasFieldOrPropertyWithValue("name", name)
        Assertions.assertThat(actual.befores)
            .hasSize(1)
        val fixtureResult = actual.befores[0]
        Assertions.assertThat(fixtureResult)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", firstName)
        Assertions.assertThat(fixtureResult.steps)
            .hasSize(2)
            .flatExtracting(extractor(StepResult::name))
            .containsExactly(firstStepName, secondStepName)
    }

    @Test
    @Throws(Exception::class)
    fun supportForConcurrentUseOfChildThreads() {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val threads = 20
        val stepsCount = 1000
        val result: TestResult = TestResult(uuid).apply {
            this.name = name
        }
        lifecycle.scheduleTestCase(result)
        lifecycle.startTestCase(uuid)
        lifecycle.startTestCase(uuid)
        val service = Executors.newFixedThreadPool(threads)
        val tasks: MutableList<Callable<Void>> = ArrayList()
        for (i in 0 until threads) {
            tasks.add(StepCall(lifecycle, i, stepsCount))
        }
        val futures = service.invokeAll(tasks)
        for (future in futures) {
            future.get()
        }
        lifecycle.stopTestCase(uuid)
        lifecycle.writeTestCase(uuid)
        val slot = slot<TestResult>()
        verify(exactly = 1) { writer.write(capture(slot)) }
        val steps: List<StepResult> = slot.captured.steps
        val expected = threads * stepsCount
        Assertions.assertThat(steps)
            .hasSize(expected)
        Assertions.assertThat(steps)
            .doesNotContain(null as StepResult?)
        val emptyNameCount = steps.asSequence()
            .map(StepResult::name)
            .filter { obj: Any? -> Objects.isNull(obj) }
            .count()
        Assertions.assertThat(emptyNameCount)
            .describedAs("All steps should have non-empty names")
            .isEqualTo(0)
        val anyMatch = steps.asSequence()
            .mapNotNull(StepResult::name)
            .any { it.matches("^Step \\d+$".toRegex()) }
        Assertions.assertThat(anyMatch)
            .describedAs("All steps names should start with Step")
            .isTrue()
        val countDistinct = steps.asSequence()
            .map(StepResult::name)
            .distinct()
            .count()
        Assertions.assertThat(countDistinct)
            .isEqualTo(expected.toLong())
    }

    private fun randomStep(parentUuid: String): String {
        val uuid = random(String::class.java)
        val name = random(String::class.java)
        val step: StepResult = StepResult().apply {
            this.name = name
        }
        lifecycle.startStep(parentUuid, uuid, step)
        lifecycle.stopStep(uuid)
        return name
    }

    private class StepCall(private val lifecycle: AllureLifecycle, private val id: Int, private val stepsCount: Int) :
        Callable<Void> {
        override fun call(): Void? {
            for (j in 0 until stepsCount) {
                val stepId = id * stepsCount + j
                val stepUuid = "step $stepId"
                val stepName = "Step $stepId"
                val step: StepResult = StepResult().apply {
                    this.name = stepName
                }
                lifecycle.startStep(stepUuid, step)
                lifecycle.stopStep(stepUuid)
            }
            return null
        }

    }
}