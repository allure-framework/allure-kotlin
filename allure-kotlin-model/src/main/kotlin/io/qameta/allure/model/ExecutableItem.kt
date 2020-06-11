package io.qameta.allure.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores common information about executable items.
 *
 */
@Serializable
abstract class ExecutableItem(
    var name: String? = null,
    var start: Long? = null,
    var stop: Long? = null,
    var stage: Stage? = null,
    var description: String? = null,
    var descriptionHtml: String? = null,
    override var status: Status? = null,
    override var statusDetails: StatusDetails? = null,
    override val steps: MutableList<StepResult> = mutableListOf(),
    override val attachments: MutableList<Attachment> = mutableListOf(),
    override val parameters: MutableList<Parameter> = mutableListOf()
) : WithAttachments, WithParameters, WithStatusDetails, WithSteps