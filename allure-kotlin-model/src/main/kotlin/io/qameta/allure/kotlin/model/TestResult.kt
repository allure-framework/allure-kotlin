package io.qameta.allure.kotlin.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores test result information.
 */
@Serializable
data class TestResult @JvmOverloads constructor(
    val uuid: String,
    var historyId: String? = null,
    var testCaseId: String? = null,
    var rerunOf: String? = null,
    var fullName: String? = null,
    val labels: MutableList<Label> = mutableListOf(),
    override val links: MutableList<Link> = mutableListOf()
) : ExecutableItem(), WithLinks