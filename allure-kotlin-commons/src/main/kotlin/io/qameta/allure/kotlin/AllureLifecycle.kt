package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.internal.AllureStorage
import io.qameta.allure.kotlin.internal.AllureThreadContext
import io.qameta.allure.kotlin.listener.*
import io.qameta.allure.kotlin.model.*
import io.qameta.allure.kotlin.model.Attachment
import io.qameta.allure.kotlin.util.PropertiesUtils
import io.qameta.allure.kotlin.util.ServiceLoaderUtils
import io.qameta.allure.kotlin.util.error
import io.qameta.allure.kotlin.util.loggerFor
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.logging.Logger

class AllureLifecycle @JvmOverloads constructor(
    private val writer: AllureResultsWriter = getDefaultWriter(),
    private val notifier: LifecycleNotifier = getDefaultNotifier()
) {
    private val storage: AllureStorage = AllureStorage()
    private val threadContext: AllureThreadContext = AllureThreadContext()

    /**
     * Starts test container with specified parent container.
     *
     * @param containerUuid the uuid of parent container.
     * @param container     the container.
     */
    fun startTestContainer(containerUuid: String, container: TestResultContainer) {
        storage.getContainer(containerUuid)?.let {
            synchronized(storage) {
                it.children.add(container.uuid)
            }
        }
        startTestContainer(container)
    }

    /**
     * Starts test container.
     *
     * @param container the container.
     */
    fun startTestContainer(container: TestResultContainer) {
        notifier.beforeContainerStart(container)
        container.start = System.currentTimeMillis()
        storage.put(container.uuid, container)
        notifier.afterContainerStart(container)
    }

    /**
     * Updates test container.
     *
     * @param uuid   the uuid of container.
     * @param updateBlock the update function.
     */
    fun updateTestContainer(uuid: String, updateBlock: (TestResultContainer) -> Unit) {
        val container = storage.getContainer(uuid) ?: return Unit.also {
            LOGGER.error("Could not update test container: container with uuid $uuid not found")
        }
        notifier.beforeContainerUpdate(container)
        updateBlock(container)
        notifier.afterContainerUpdate(container)
    }

    /**
     * Stops test container by given uuid.
     *
     * @param uuid the uuid of container.
     */
    fun stopTestContainer(uuid: String) {
        val container = storage.getContainer(uuid) ?: return Unit.also {
            LOGGER.error("Could not stop test container: container with uuid $uuid not found")
        }
        notifier.beforeContainerStop(container)
        container.stop = System.currentTimeMillis()
        notifier.afterContainerUpdate(container)
    }

    /**
     * Writes test container with given uuid.
     *
     * @param uuid the uuid of container.
     */
    fun writeTestContainer(uuid: String) {
        val container = storage.getContainer(uuid) ?: return Unit.also {
            LOGGER.error("Could not write test container: container with uuid $uuid not found")
        }
        notifier.beforeContainerWrite(container)
        writer.write(container)
        storage.remove(uuid)
        notifier.afterContainerWrite(container)
    }

    /**
     * Start a new prepare fixture with given parent.
     *
     * @param containerUuid the uuid of parent container.
     * @param uuid          the fixture uuid.
     * @param result        the fixture.
     */
    fun startPrepareFixture(containerUuid: String, uuid: String, result: FixtureResult) {
        storage.getContainer(containerUuid)?.let {
            synchronized(storage) {
                it.befores.add(result)
            }
        }
        notifier.beforeFixtureStart(result)
        startFixture(uuid, result)
        notifier.afterFixtureStart(result)
    }


    /**
     * Start a new tear down fixture with given parent.
     *
     * @param containerUuid the uuid of parent container.
     * @param uuid          the fixture uuid.
     * @param result        the fixture.
     */
    fun startTearDownFixture(containerUuid: String, uuid: String, result: FixtureResult) {
        storage.getContainer(containerUuid)?.let {
            synchronized(storage) {
                it.afters.add(result)
            }
        }
        notifier.beforeFixtureStart(result)
        startFixture(uuid, result)
        notifier.afterFixtureStart(result)
    }

    /**
     * Start a new fixture with given uuid.
     *
     * @param uuid   the uuid of fixture.
     * @param result the test fixture.
     */
    private fun startFixture(uuid: String, result: FixtureResult) {
        storage.put(uuid, result)
        result.stage = Stage.RUNNING
        result.start = System.currentTimeMillis()
        threadContext.clear()
        threadContext.start(uuid)
    }

    /**
     * Updates current running fixture. Shortcut for [updateFixture].
     *
     * @param updateBlock the update function.
     */
    fun updateFixture(updateBlock: (FixtureResult) -> Unit) {
        val uuid = threadContext.root ?: return Unit.also {
            LOGGER.error("Could not update test fixture: no test fixture running")
        }
        updateFixture(uuid, updateBlock)
    }

    /**
     * Updates fixture by given uuid.
     *
     * @param uuid   the uuid of fixture.
     * @param updateBlock the update function.
     */
    fun updateFixture(uuid: String, updateBlock: (FixtureResult) -> Unit) {
        val fixture = storage.getFixture(uuid) ?: return Unit.also {
            LOGGER.error("Could not update test fixture: test fixture with uuid $uuid not found")
        }
        notifier.beforeFixtureUpdate(fixture)
        updateBlock(fixture)
        notifier.afterFixtureUpdate(fixture)
    }

    /**
     * Stops fixture by given uuid.
     *
     * @param uuid the uuid of fixture.
     */
    fun stopFixture(uuid: String) {
        val fixture = storage.getFixture(uuid) ?: return Unit.also {
            LOGGER.error("Could not update test fixture: test fixture with uuid $uuid not found")
        }
        notifier.beforeFixtureStop(fixture)
        fixture.stage = Stage.FINISHED
        fixture.stop = System.currentTimeMillis()
        storage.remove(uuid)
        threadContext.clear()
        notifier.afterFixtureStop(fixture)
    }


    /**
     * Returns uuid of current running test case if any.
     *
     * @return the uuid of current running test case.
     */
    fun getCurrentTestCase(): String? {
        return threadContext.root
    }

    /**
     * Returns uuid of current running test case or step if any.
     *
     * @return the uuid of current running test case or step.
     */
    fun getCurrentTestCaseOrStep(): String? {
        return threadContext.current
    }

    /**
     * Sets specified test case uuid as current. Note that
     * test case with such uuid should be created and existed in storage, otherwise
     * method take no effect.
     *
     * @param uuid the uuid of test case.
     * @return true if current test case was configured successfully, false otherwise.
     */
    fun setCurrentTestCase(uuid: String): Boolean {
        val found = storage.getTestResult(uuid) ?: return false
        threadContext.clear()
        threadContext.start(uuid)
        return true
    }

    /**
     * Schedules test case with given parent.
     *
     * @param containerUuid the uuid of container.
     * @param result        the test case to schedule.
     */
    fun scheduleTestCase(containerUuid: String, result: TestResult) {
        storage.getContainer(containerUuid)?.let {
            synchronized(storage) {
                it.children.add(result.uuid)
            }
        }
        scheduleTestCase(result)
    }

    /**
     * Schedule given test case.
     *
     * @param result the test case to schedule.
     */
    fun scheduleTestCase(result: TestResult) {
        notifier.beforeTestSchedule(result)
        result.stage = Stage.SCHEDULED
        storage.put(result.uuid, result)
        notifier.afterTestSchedule(result)
    }

    /**
     * Starts test case with given uuid. In order to start test case it should be scheduled at first.
     *
     * @param uuid the uuid of test case to start.
     */
    fun startTestCase(uuid: String) {
        threadContext.clear()
        val testResult = storage.getTestResult(uuid) ?: return Unit.also {
            LOGGER.error("Could not start test case: test case with uuid $uuid is not scheduled")
        }
        notifier.beforeTestStart(testResult)
        with(testResult) {
            stage = Stage.RUNNING
            start = System.currentTimeMillis()
        }
        threadContext.start(uuid)
        notifier.afterTestStart(testResult)
    }

    /**
     * Shortcut for [updateTestCase] for current running test case uuid.
     *
     * @param update the update function.
     */
    fun updateTestCase(update: (TestResult) -> Unit) {
        val uuid = threadContext.root ?: return Unit.also {
            LOGGER.error("Could not update test case: no test case running")
        }
        updateTestCase(uuid, update)
    }

    /**
     * Updates test case by given uuid.
     *
     * @param uuid   the uuid of test case to update.
     * @param update the update function.
     */
    fun updateTestCase(uuid: String, update: (TestResult) -> Unit) {
        val testResult = storage.getTestResult(uuid) ?: return Unit.also {
            LOGGER.error("Could not update test case: test case with uuid $uuid not found")
        }
        notifier.beforeTestUpdate(testResult)
        update(testResult)
        notifier.afterTestUpdate(testResult)
    }

    /**
     * Stops test case by given uuid. Test case marked as [Stage.FINISHED] and also
     * stop timestamp is calculated. Result would be stored in memory until
     * [.writeTestCase] method is called. Also stopped test case could be
     * updated by [.updateTestCase] method.
     *
     * @param uuid the uuid of test case to stop.
     */
    fun stopTestCase(uuid: String) {
        val testResult = storage.getTestResult(uuid) ?: return Unit.also {
            LOGGER.error("Could not stop test case: test case with uuid $uuid not found")
        }

        notifier.beforeTestStop(testResult)
        with(testResult) {
            stage = Stage.FINISHED
            stop = System.currentTimeMillis()
        }
        threadContext.clear()
        notifier.afterTestStop(testResult)
    }

    /**
     * Writes test case with given uuid using configured [AllureResultsWriter].
     *
     * @param uuid the uuid of test case to write.
     */
    fun writeTestCase(uuid: String) {
        val testResult = storage.getTestResult(uuid) ?: return Unit.also {
            LOGGER.error("Could not write test case: test case with uuid $uuid not found")
        }
        notifier.beforeTestWrite(testResult)
        writer.write(testResult)
        storage.remove(uuid)
        notifier.afterTestWrite(testResult)
    }

    /**
     * Start a new step as child step of current running test case or step. Shortcut
     * for [.startStep].
     *
     * @param uuid   the uuid of step.
     * @param result the step.
     */
    fun startStep(uuid: String, result: StepResult) {
        val parentUuid = threadContext.current ?: return Unit.also {
            LOGGER.error("Could not start step: no test case running")
        }
        startStep(parentUuid, uuid, result)
    }

    /**
     * Start a new step as child of specified parent.
     *
     * @param parentUuid the uuid of parent test case or step.
     * @param uuid       the uuid of step.
     * @param result     the step.
     */
    fun startStep(parentUuid: String, uuid: String, result: StepResult) {
        notifier.beforeStepStart(result)
        with(result) {
            stage = Stage.RUNNING
            start = System.currentTimeMillis()
        }
        threadContext.start(uuid)
        storage.put(uuid, result)
        storage.get<WithSteps>(parentUuid)?.let { parentStep ->
            synchronized(storage) {
                parentStep.steps.add(result)
            }
        }
        notifier.afterStepStart(result)
    }

    /**
     * Updates current step. Shortcut for [.updateStep].
     *
     * @param update the update function.
     */
    fun updateStep(update: (StepResult) -> Unit) {
        val uuid = threadContext.current ?: return Unit.also {
            LOGGER.error("Could not update step: no step running")
        }
        updateStep(uuid, update)
    }

    /**
     * Updates step by specified uuid.
     *
     * @param uuid   the uuid of step.
     * @param update the update function.
     */
    fun updateStep(uuid: String, update: (StepResult) -> Unit) {
        val step = storage.getStep(uuid) ?: return Unit.also {
            LOGGER.error("Could not update step: step with uuid $uuid not found")
        }
        notifier.beforeStepUpdate(step)
        update(step)
        notifier.afterStepUpdate(step)
    }

    /**
     * Stops current running step. Shortcut for [.stopStep].
     */
    fun stopStep() {
        val root: String? = threadContext.root
        val uuid = threadContext.current.takeIf { it != root } ?: return Unit.also {
            LOGGER.error("Could not stop step: no step running")
        }
        stopStep(uuid)
    }

    fun stopStep(uuid: String) {
        val step = storage.getStep(uuid) ?: return Unit.also {
            LOGGER.error("Could not stop step: step with uuid $uuid not found")
        }

        notifier.beforeStepStop(step)
        with(step) {
            stage = Stage.FINISHED
            stop = System.currentTimeMillis()
        }
        storage.remove(uuid)
        threadContext.stop()
        notifier.afterStepStop(step)
    }


    /**
     * Adds attachment into current test or step if any exists. Shortcut
     * for [addAttachment]
     *
     * @param name          the name of attachment
     * @param type          the content type of attachment
     * @param fileExtension the attachment file extension
     * @param body          attachment content
     */
    fun addAttachment(name: String, body: ByteArray, type: String?, fileExtension: String?) {
        addAttachment(
            name = name,
            stream = ByteArrayInputStream(body),
            type = type,
            fileExtension = fileExtension
        )
    }

    /**
     * Adds attachment to current running test or step.
     *
     * @param name          the name of attachment
     * @param type          the content type of attachment
     * @param fileExtension the attachment file extension
     * @param stream        attachment content
     */
    fun addAttachment(name: String, stream: InputStream, type: String?, fileExtension: String?) {
        writeAttachment(
            attachmentSource = prepareAttachment(
                name = name,
                type = type,
                fileExtension = fileExtension
            ),
            stream = stream
        )
    }

    fun prepareAttachment(
        name: String,
        type: String?,
        fileExtension: String?
    ): String {
        val extension = fileExtension
            ?.takeIf { it.isNotEmpty() }
            ?.let { if (it[0] == '.') it else ".$it" }
            ?: ""
        val source =
            UUID.randomUUID().toString() + AllureConstants.ATTACHMENT_FILE_SUFFIX + extension
        val uuid = threadContext.current ?: return source.also {
            LOGGER.error("Could not add attachment: no test is running")
        }
        val attachment = Attachment(
            source = source,
            name = name.takeIf { it.isNotEmpty() },
            type = type?.takeIf { it.isNotEmpty() }
        )
        storage.get<WithAttachments>(uuid)?.let {
            synchronized(storage) {
                it.attachments.add(attachment)
            }
        }
        return attachment.source
    }

    /**
     * Writes attachment with specified source.
     *
     * @param attachmentSource the source of attachment.
     * @param stream           the attachment content.
     */
    fun writeAttachment(attachmentSource: String, stream: InputStream) {
        writer.write(attachmentSource, stream)
    }

    companion object {

        private val LOGGER: Logger = loggerFor<AllureLifecycle>()

        private fun getDefaultWriter(): FileSystemResultsWriter {
            val path = PropertiesUtils.resultsDirectoryPath
            return FileSystemResultsWriter(File(path))
        }

        private fun getDefaultNotifier(): LifecycleNotifier {
            val classLoader = Thread.currentThread().contextClassLoader
            return LifecycleNotifier(
                containerListeners = ServiceLoaderUtils.load(ContainerLifecycleListener::class.java, classLoader),
                testListeners = ServiceLoaderUtils.load(TestLifecycleListener::class.java, classLoader),
                fixtureListeners = ServiceLoaderUtils.load(FixtureLifecycleListener::class.java, classLoader),
                stepListeners = ServiceLoaderUtils.load(StepLifecycleListener::class.java, classLoader)
            )
        }
    }
}