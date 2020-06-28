package io.qameta.allure.sample.junit4.android

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.qameta.allure.android.rules.LogcatRule
import io.qameta.allure.android.rules.ScreenshotRule
import io.qameta.allure.android.rules.WindowHierarchyRule
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.junit4.DisplayName
import io.qameta.allure.kotlin.junit4.Tag
import org.junit.*
import org.junit.rules.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
@Epic("Samples")
@DisplayName("SampleActivity failure tests")
@Tag("Instrumentation")
class SampleActivityIgnoreTest {

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(LogcatRule())
        .around(ScreenshotRule(mode = ScreenshotRule.Mode.END, screenshotName = "screenshot-finish"))
        .around(WindowHierarchyRule())

    @Before
    fun setup() {
        ActivityScenario.launch(SampleActivity::class.java)
    }

    @Test
    @Ignore("Ignore example")
    @Description("Empty test that ought to be ignored")
    fun shouldIgnore() {
    }

}