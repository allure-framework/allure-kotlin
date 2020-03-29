package io.qameta.allure.kotlin.model

interface WithStatusDetails : WithStatus {
    val statusDetails: StatusDetails?
}