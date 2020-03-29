package io.qameta.allure.kotlin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Test statuses.
 */
@Serializable
enum class Status {
    @SerialName("failed")
    FAILED,
    @SerialName("broken")
    BROKEN,
    @SerialName("passed")
    PASSED,
    @SerialName("skipped")
    SKIPPED;
}