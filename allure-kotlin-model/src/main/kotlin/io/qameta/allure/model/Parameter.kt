package io.qameta.allure.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores parameter information.
 */
@Serializable
data class Parameter(
    var name: String? = null,
    var value: String? = null
)