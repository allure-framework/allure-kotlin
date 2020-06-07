package io.qameta.allure.kotlin.model

interface WithSteps {
    val steps: MutableList<StepResult>
}