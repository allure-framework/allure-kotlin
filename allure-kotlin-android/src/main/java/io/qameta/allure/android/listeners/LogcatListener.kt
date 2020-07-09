package io.qameta.allure.android.listeners

import android.util.Log
import io.qameta.allure.kotlin.listener.ContainerLifecycleListener
import io.qameta.allure.kotlin.listener.StepLifecycleListener
import io.qameta.allure.kotlin.listener.TestLifecycleListener
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.model.TestResult
import io.qameta.allure.kotlin.model.TestResultContainer

class LogcatListener :
    ContainerLifecycleListener by LogcatContainerListener(),
    TestLifecycleListener by LogcatTestLifecycleListener(),
    StepLifecycleListener by LogcatStepLifecycleListener()

class LogcatContainerListener : ContainerLifecycleListener {
    override fun beforeContainerStart(container: TestResultContainer) {
        container.log("START")
    }

    override fun beforeContainerUpdate(container: TestResultContainer) {
        container.log("UPDATE")
    }

    override fun beforeContainerStop(container: TestResultContainer) {
        container.log("STOP")
    }

    override fun beforeContainerWrite(container: TestResultContainer) {
        container.log("WRITE")
    }

    private fun TestResultContainer.log(action: String) {
        Log.d(TAG, "$action container: $uuid")
    }

    companion object {
        private const val TAG = "LogcatContainerListener"
    }
}

class LogcatTestLifecycleListener : TestLifecycleListener {

    override fun beforeTestSchedule(result: TestResult) {
        result.log("SCHEDULE")
    }

    override fun beforeTestStart(result: TestResult) {
        result.log("START")
    }

    override fun beforeTestUpdate(result: TestResult) {
        result.log("UPDATE")
    }

    override fun beforeTestStop(result: TestResult) {
        result.log("STOP")
    }

    override fun beforeTestWrite(result: TestResult) {
        result.log("WRITE")
    }

    private fun TestResult.log(action: String) {
        Log.d(TAG, "$action test: $uuid ($fullName)")
    }

    companion object {
        private const val TAG = "LogcatTestLifecycle"
    }
}

class LogcatStepLifecycleListener : StepLifecycleListener {

    override fun beforeStepStart(result: StepResult) {
        result.log("START")
    }

    override fun beforeStepUpdate(result: StepResult) {
        result.log("UPDATE")
    }

    override fun beforeStepStop(result: StepResult) {
        result.log("STOP")
    }

    private fun StepResult.log(action: String) {
        Log.d(TAG, "$action step: $name")
    }

    companion object {
        private const val TAG = "LogcatStepLifecycle"
    }
}

