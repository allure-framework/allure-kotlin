package io.qameta.allure.junit4.samples

import io.qameta.allure.*
import org.junit.Test

class TestWithAnnotations {
    @Test
    @Epics(
        Epic("epic1"),
        Epic("epic2"),
        Epic("epic3")
    )
    @Features(
        Feature("feature1"),
        Feature("feature2"),
        Feature("feature3")
    )
    @Stories(
        Story("story1"),
        Story("story2"),
        Story("story3")
    )
    @Owner("some-owner")
    @Throws(Exception::class)
    fun someTest() {
    }
}