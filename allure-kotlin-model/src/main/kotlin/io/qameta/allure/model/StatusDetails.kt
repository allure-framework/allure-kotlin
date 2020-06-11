package io.qameta.allure.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores status details.
 */
@Serializable
data class StatusDetails(
    var known: Boolean = false,
    var muted: Boolean = false,
    var flaky: Boolean = false,
    var message: String? = null,
    var trace: String? = null
)