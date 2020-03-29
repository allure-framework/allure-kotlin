package io.qameta.allure.kotlin.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores label information.
 */
@Serializable
data class Label(
    var name: String? = null,
    var value: String? = null
)