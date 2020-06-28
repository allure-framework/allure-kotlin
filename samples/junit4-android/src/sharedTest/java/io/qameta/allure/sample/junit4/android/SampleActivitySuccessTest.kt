package io.qameta.allure.sample.junit4.android

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.qameta.allure.android.allureScreenshot
import io.qameta.allure.android.rules.LogcatRule
import io.qameta.allure.android.rules.ScreenshotRule
import io.qameta.allure.android.rules.WindowHierarchyRule
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import io.qameta.allure.kotlin.Allure.description
import io.qameta.allure.kotlin.Allure.feature
import io.qameta.allure.kotlin.Allure.issue
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Allure.story
import io.qameta.allure.kotlin.Allure.tms
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Issue
import io.qameta.allure.kotlin.Lead
import io.qameta.allure.kotlin.Owner
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
@DisplayName("SampleActivity success tests")
@Tag("Instrumentation")
class SampleActivitySuccessTest {

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(LogcatRule())
        .around(ScreenshotRule(mode = ScreenshotRule.Mode.END, screenshotName = "screenshot-finish"))
        .around(WindowHierarchyRule())

    @Before
    fun setup() {
        ActivityScenario.launch(SampleActivity::class.java)
    }

    @Test
    @Feature("Successes")
    @Story("Annotations")
    @Issue("JIRA-123")
    @TmsLink("TR-123")
    @Owner("Some owner")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("should text change after click with annotations")
    @Description("Verification of text changing after interaction with a button")
    fun shouldTextChangeAfterClickWithAnnotations() {
        step("Click main button") {
            allureScreenshot("initial_state")
            Espresso.onView(withId(R.id.button_some)).perform(click())
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        step("Verify text") {
            Espresso.onView(withId(R.id.label_some))
                .check(matches(withText(context.getString(R.string.after_click))))
        }
    }

    @Test
    @Lead("Some lead")
    @Owner("Some owner")
    @Severity(value = SeverityLevel.BLOCKER)
    @DisplayName("should text change after click with methods")
    fun shouldTextChangeAfterClickWithMethods() {
        feature("Successes")
        story("Methods")
        issue(name = "JIRA-123")
        tms(name = "TR-123")
        description("Verification of text changing after interaction with a button")

        step("Click main button") {
            allureScreenshot("initial_state")
            Espresso.onView(withId(R.id.button_some)).perform(click())
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        step("Verify text") {
            Espresso.onView(withId(R.id.label_some))
                .check(matches(withText(context.getString(R.string.after_click))))
        }
    }

}

