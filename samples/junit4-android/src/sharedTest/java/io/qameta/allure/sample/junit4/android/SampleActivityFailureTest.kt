package io.qameta.allure.sample.junit4.android

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import io.qameta.allure.android.rules.LogcatRule
import io.qameta.allure.android.rules.ScreenshotRule
import io.qameta.allure.android.rules.WindowHierarchyRule
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Issue
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.TmsLink
import io.qameta.allure.kotlin.junit4.DisplayName
import io.qameta.allure.kotlin.junit4.Tag
import org.junit.*
import org.junit.rules.*
import org.junit.runner.*

@RunWith(AllureAndroidJUnit4::class)
@Epic("Samples")
@DisplayName("SampleActivity ignore tests")
@Tag("Instrumentation")
class SampleActivityFailureTest {

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(LogcatRule())
        .around(ScreenshotRule(mode = ScreenshotRule.Mode.FAILURE, screenshotName = "screenshot-failure"))
        .around(WindowHierarchyRule())

    @Before
    fun setup() {
        ActivityScenario.launch(SampleActivity::class.java)
    }

    @Test
    @Feature("Failures")
    @Story("Initial state")
    @Issue("JIRA-456")
    @TmsLink("TR-456")
    @Severity(value = SeverityLevel.CRITICAL)
    @DisplayName("should initial state fail")
    @Description("Verification of initial state that should fail on purpose (just so it is shown as a broken test by Allure)")
    fun shouldInitialStateFail() {
        Allure.step("Verify init text state") {
            Espresso.onView(ViewMatchers.withId(R.id.label_some))
                .check(ViewAssertions.matches(ViewMatchers.withText("FOO")))

        }
    }

}