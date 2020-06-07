package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.model.*
import io.qameta.allure.kotlin.model.Link
import io.qameta.allure.kotlin.util.ExceptionUtils
import io.qameta.allure.kotlin.util.ResultsUtils
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * The class contains some useful methods to work with [AllureLifecycle].
 */
object Allure {

    private const val TXT_EXTENSION = ".txt"
    private const val TEXT_PLAIN = "text/plain"
    @JvmStatic
    var lifecycle: AllureLifecycle = AllureLifecycle()

    /**
     * Adds step with provided name and status in current test or step (or test fixture). Takes no effect
     * if no test run at the moment.
     *
     * @param name   the name of step.
     * @param status the step status.
     */
    @JvmOverloads
    @JvmStatic
    fun step(name: String, status: Status = Status.PASSED) {
        val uuid = UUID.randomUUID().toString()
        val step = StepResult().apply {
            this.name = name
            this.status = status
        }
        lifecycle.startStep(uuid, step)
        lifecycle.stopStep(uuid)
    }

    /**
     * Run provided [block] as step with given name. Takes no effect if no test run at the moment.
     *
     * @param block the step's body.
     */
    @JvmOverloads
    @JvmStatic
    fun <T> step(
        name: String = "step",
        block: StepContext.() -> T
    ): T {
        val uuid = UUID.randomUUID().toString()
        lifecycle.startStep(uuid, StepResult().apply {
            this.name = name
        })

        return try {
            block(DefaultStepContext(uuid)).also {
                lifecycle.updateStep(uuid) { it.status = Status.PASSED }
            }
        } catch (throwable: Throwable) {
            lifecycle.updateStep {
                with(it) {
                    status = ResultsUtils.getStatus(throwable) ?: Status.BROKEN
                    statusDetails = ResultsUtils.getStatusDetails(throwable)
                }
            }
            ExceptionUtils.sneakyThrow<RuntimeException>(throwable)
        } finally {
            lifecycle.stopStep(uuid)
        }
    }

    /**
     * Adds epic label to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Shortcut for [label].
     *
     * @param value the value of label.
     */
    @JvmStatic
    fun epic(value: String) {
        label(ResultsUtils.EPIC_LABEL_NAME, value)
    }

    /**
     * Adds feature label to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Shortcut for [label].
     *
     * @param value the value of label.
     */
    @JvmStatic
    fun feature(value: String) {
        label(ResultsUtils.FEATURE_LABEL_NAME, value)
    }

    /**
     * Adds story label to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Shortcut for [label].
     *
     * @param value the value of label.
     */
    @JvmStatic
    fun story(value: String) {
        label(ResultsUtils.STORY_LABEL_NAME, value)
    }

    /**
     * Adds suite label to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Shortcut for [label].
     *
     * @param value the value of label.
     */
    @JvmStatic
    fun suite(value: String) {
        label(ResultsUtils.SUITE_LABEL_NAME, value)
    }

    /**
     * Adds label to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment.
     *
     * @param name  the name of label.
     * @param value the value of label.
     */
    @JvmStatic
    fun label(name: String, value: String) {
        val label = Label(name = name, value = value)
        lifecycle.updateTestCase { it.labels.add(label) }
    }

    /**
     * Adds parameter to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment.
     *
     * @param name  the name of parameter.
     * @param value the value of parameter.
     */
    @JvmStatic
    fun <T> parameter(name: String, value: T): T {
        val parameter = ResultsUtils.createParameter(name, value)
        lifecycle.updateTestCase { it.parameters.add(parameter) }
        return value
    }

    /**
     * Adds issue link to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Shortcut for [link].
     *
     * @param name the name of link.
     * @param url  the link's url.
     */
    @JvmOverloads
    @JvmStatic
    fun issue(name: String, url: String = "") {
        link(url = url, name = name, type = ResultsUtils.ISSUE_LINK_TYPE)
    }

    /**
     * Adds tms link to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Shortcut for [link].
     *
     * @param name the name of link.
     * @param url  the link's url.
     */
    @JvmOverloads
    @JvmStatic
    fun tms(name: String, url: String = "") {
        link(url = url, name = name, type = ResultsUtils.TMS_LINK_TYPE)
    }

    /**
     * Adds link to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment.
     *
     * @param url  the link's url.
     * @param name the name of link.
     * @param type the type of link, used to display link icon in the report.
     */
    @JvmOverloads
    @JvmStatic
    fun link(url: String, name: String = "", type: String = "") {
        val link = Link(
            name = name.ifBlank { null },
            url = url.ifBlank { null },
            type = type.ifBlank { null }
        )
        lifecycle.updateTestCase { it.links.add(link) }
    }

    /**
     * Adds description to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Expecting description provided in Markdown format.
     *
     * @param description the description in markdown format.
     * @see descriptionHtml
     */
    @JvmStatic
    fun description(description: String) {
        lifecycle.updateTestCase { executable: TestResult ->
            executable.description = description
        }
    }

    /**
     * Adds descriptionHtml to current test or step (or fixture) if any. Takes no effect
     * if no test run at the moment. Note that description will take no effect if descriptionHtml is
     * specified.
     *
     * @param descriptionHtml the description in html format.
     * @see description
     */
    @JvmStatic
    fun descriptionHtml(descriptionHtml: String) {
        lifecycle.updateTestCase { executable: TestResult ->
            executable.descriptionHtml = descriptionHtml
        }
    }

    /**
     * Adds attachment with text content.
     *
     * @param name    the name of attachment.
     * @param content the attachment content.
     * @param type the attachment type, be default [TEXT_PLAIN].
     * @param fileExtension the file extension of attachment, be default [TXT_EXTENSION].
     */
    @JvmOverloads
    @JvmStatic
    fun attachment(
        name: String,
        content: String,
        type: String = TEXT_PLAIN,
        fileExtension: String = TXT_EXTENSION
    ) {
        lifecycle.addAttachment(
            name = name,
            body = content.toByteArray(StandardCharsets.UTF_8),
            type = type,
            fileExtension = fileExtension
        )
    }

    /**
     * Adds attachment with stream content.
     *
     * @param name    the name of attachment.
     * @param content the stream that contains attachment content.
     * @param type the attachment type.
     * @param fileExtension the file extension of attachment.
     */
    @JvmOverloads
    @JvmStatic
    fun attachment(
        name: String,
        content: InputStream,
        type: String? = null,
        fileExtension: String? = null
    ) {
        lifecycle.addAttachment(
            name = name,
            stream = content,
            type = type,
            fileExtension = fileExtension
        )
    }

    /**
     * Step context.
     */
    interface StepContext {
        fun name(name: String)
        fun <T> parameter(name: String, value: T): T
    }

    /**
     * Basic implementation of step context.
     */
    private class DefaultStepContext(private val uuid: String) : StepContext {
        override fun name(name: String) {
            lifecycle.updateStep(uuid) { it.name = name }
        }

        override fun <T> parameter(name: String, value: T): T {
            val param = ResultsUtils.createParameter(name, value)
            lifecycle.updateStep(uuid) { it.parameters.add(param) }
            return value
        }

    }

}