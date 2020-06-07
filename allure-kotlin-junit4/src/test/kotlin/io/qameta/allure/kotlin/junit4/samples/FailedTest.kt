package io.qameta.allure.kotlin.junit4.samples

import io.qameta.allure.kotlin.*
import org.assertj.core.api.Assertions
import org.junit.Test

class FailedTest {
    @Test
    @Links(
        Link("link-1"),
        Link("link-2")
    )
    @Issues(
        Issue("issue-1"),
        Issue("issue-2")
    )
    @TmsLinks(
        TmsLink("tms-1"),
        TmsLink("tms-2")
    )
    @Throws(Exception::class)
    fun failedTest() {
        Assertions.assertThat(true).isFalse()
    }
}