package io.qameta.allure.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores attachment information.
 */
@Serializable
data class Attachment(
    val source: String,
    var name: String? = null,
    var type: String? = null
)