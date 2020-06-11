package io.qameta.allure.model

interface WithSteps {
    val steps: MutableList<StepResult>
}