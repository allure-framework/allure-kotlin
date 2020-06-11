package io.qameta.allure.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores link information.
 */
@Serializable
data class Link(
    var name: String? = null,
    var url: String? = null,
    var type: String? = null
)