package io.qameta.allure.model

interface WithStatusDetails : WithStatus {
    val statusDetails: StatusDetails?
}