package io.qameta.allure.kotlin.junit4.samples

import io.qameta.allure.kotlin.junit4.DisplayName
import io.qameta.allure.kotlin.junit4.samples.TestBasedOnSampleRunner.SampleRunnerBasedOnNotClasses
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

@RunWith(SampleRunnerBasedOnNotClasses::class)
class TestBasedOnSampleRunner {
    class SampleRunnerBasedOnNotClasses(testClass: Class<*>?) : Runner() {
        override fun getDescription(): Description {
            return Description.createTestDescription(
                "allure junit4 runner.test for non-existing classes (would be a class in normal runner)",
                "should correctly handle non-existing classes (would be method name in normal runner)",
                DisplayName::class.constructors.first().call("Some human readable name")
            )
        }

        override fun run(notifier: RunNotifier) {
            notifier.fireTestStarted(description)
            notifier.fireTestFinished(description)
        }
    }
}