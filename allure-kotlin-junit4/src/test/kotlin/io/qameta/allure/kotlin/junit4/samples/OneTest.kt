package io.qameta.allure.kotlin.junit4.samples

import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

@DisplayName("Should be overwritten by method annotation")
class OneTest {
    @Test
    @DisplayName("Simple test")
    @Description("Description here")
    fun simpleTest() {
    }
}