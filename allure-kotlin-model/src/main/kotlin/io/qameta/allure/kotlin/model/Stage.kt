package io.qameta.allure.kotlin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Test stages.
 */
@Serializable
enum class Stage {
    @SerialName("scheduled")
    SCHEDULED,
    @SerialName("running")
    RUNNING,
    @SerialName("finished")
    FINISHED,
    @SerialName("pending")
    PENDING,
    @SerialName("interrupted")
    INTERRUPTED;

}