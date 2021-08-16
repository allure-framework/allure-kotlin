package io.qameta.allure.sample.junit4.android

import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.listener.TestLifecycleListener
import io.qameta.allure.kotlin.model.StatusDetails
import io.qameta.allure.kotlin.model.TestResult
import java.lang.RuntimeException

class ExceptionFixListener : TestLifecycleListener {

    override fun beforeTestSchedule(result: TestResult) { //do nothing
    }

    override fun afterTestSchedule(result: TestResult) { //do nothing
    }

    override fun beforeTestUpdate(result: TestResult) { //do nothing
    }

    override fun afterTestUpdate(result: TestResult) { //do nothing
    }

    override fun beforeTestStart(result: TestResult) { //do nothing
    }

    override fun afterTestStart(result: TestResult) { //do nothing
    }

    override fun beforeTestStop(result: TestResult) { //do nothing
    }

    override fun afterTestStop(result: TestResult) {
    }

    override fun beforeTestWrite(result: TestResult) {
        result.statusDetails?.apply {
            val updated = StatusDetails(
                message = substring(this.message, 10),
                trace = sublist(this.trace, 5),
                flaky = this.flaky,
                known = this.known,
                muted = this.muted
            )
            result.statusDetails = updated
        }
    }

    override fun afterTestWrite(result: TestResult) {
    }

    fun substring(message: String?, length: Int): String? {
        return message?.apply {
            return this.substring(0, this.length.coerceAtMost(length))
        }
    }

    fun sublist(message: String?, length: Int): String? {
        return message?.apply {
            val lines = this.split("\n");
            return lines.subList(0, lines.size.coerceAtMost(length)).joinToString("\n")
        }
    }


}