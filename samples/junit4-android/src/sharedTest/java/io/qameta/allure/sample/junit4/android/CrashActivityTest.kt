package io.qameta.allure.sample.junit4.android

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import io.qameta.allure.android.rules.LogcatRule
import io.qameta.allure.android.rules.ScreenshotRule
import io.qameta.allure.android.rules.WindowHierarchyRule
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.junit4.DisplayName
import io.qameta.allure.kotlin.junit4.Tag
import org.junit.*
import org.junit.rules.*
import org.junit.runner.*

@RunWith(AllureAndroidJUnit4::class)
@Epic("Samples")
@DisplayName("CrashActivity crash test")
@Tag("Instrumentation")
class CrashActivityTest {

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(LogcatRule())
        .around(ScreenshotRule(mode = ScreenshotRule.Mode.FAILURE, screenshotName = "screenshot-failure"))
        .around(WindowHierarchyRule())

    @Before
    fun setup() {
        ActivityScenario.launch(CrashActivity::class.java)
    }

    @Test
    @Feature("Failures")
    @Story("App crashes reported")
    @Severity(value = SeverityLevel.MINOR)
    @DisplayName("should provide report after crash")
    @Description("Verification that app crash doesn't affect reporting")
    fun shouldProvideReportAfterCrash() {
        step("Click crash button") {
            Espresso.onView(ViewMatchers.withId(R.id.button_crash_immediate))
                .perform(ViewActions.click())

        }
    }

    /**
     * TODO
     * When app crashes in the background, the allure results are not saved.
     * That happens due to RunListener not invoking test finish/failure callbacks.
     * This seems like Android Instrumentation bug, but needs further investigation.
     */
    @Test
    @Feature("Failures")
    @Story("App crashes reported")
    @Severity(value = SeverityLevel.MINOR)
    @DisplayName("should provide report after crash asynchronously")
    @Description("Verification that app crash doesn't affect reporting when crash happens asynchronously")
    fun shouldProvideReportAfterCrashAsync() {
        step("Click crash button") {
            Espresso.onView(ViewMatchers.withId(R.id.button_crash_async))
                .perform(ViewActions.click())
        }
        step("Wait for the crash") {
            Thread.sleep(CrashActivity.ASYNC_CRASH_TIME_MS * 2)
        }
    }

}
