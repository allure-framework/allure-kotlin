package io.qameta.allure.kotlin.util

import io.github.glytching.junit.extension.system.SystemProperties
import io.github.glytching.junit.extension.system.SystemProperty
import io.qameta.allure.kotlin.*
import io.qameta.allure.kotlin.model.Label
import io.qameta.allure.kotlin.model.Link
import io.qameta.allure.kotlin.test.function
import io.qameta.allure.kotlin.util.AnnotationUtils.getLabels
import io.qameta.allure.kotlin.util.AnnotationUtils.getLinks
import io.qameta.allure.kotlin.util.AnnotationUtilsTest.Features.FeatureA.Story1
import io.qameta.allure.kotlin.util.AnnotationUtilsTest.Features.FeatureB.Story2
import io.qameta.allure.kotlin.util.ResultsUtils.EPIC_LABEL_NAME
import io.qameta.allure.kotlin.util.ResultsUtils.FEATURE_LABEL_NAME
import io.qameta.allure.kotlin.util.ResultsUtils.STORY_LABEL_NAME
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.Resources

class AnnotationUtilsTest {
    @Epic("e1")
    @Feature("f1")
    @Story("s1")
    internal inner class WithBddAnnotations

    @Test
    fun shouldExtractDefaultLabels() {
        val labels = getLabels(WithBddAnnotations::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple(EPIC_LABEL_NAME, "e1"),
                Assertions.tuple(FEATURE_LABEL_NAME, "f1"),
                Assertions.tuple(STORY_LABEL_NAME, "s1")
            )
    }

    @Epics(Epic("e1"), Epic("e2"))
    internal inner class DirectRepeatableAnnotations

    @Test
    fun shouldExtractDirectRepeatableLabels() {
        val labels = getLabels(DirectRepeatableAnnotations::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple(EPIC_LABEL_NAME, "e1"),
                Assertions.tuple(EPIC_LABEL_NAME, "e2")
            )
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @LabelAnnotation(name = "custom")
    internal annotation class Custom(val value: String)

    @Custom("Some")
    internal inner class CustomAnnotation

    @Test
    fun shouldExtractCustomAnnotations() {
        val labels = getLabels(CustomAnnotation::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple("custom", "Some")
            )
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @LabelAnnotation(name = "custom")
    internal annotation class CustomMultiValue(vararg val value: String)

    @CustomMultiValue("First", "Second")
    internal inner class CustomMultiValueAnnotation

    @Test
    fun shouldSupportMultiValueAnnotations() {
        val labels = getLabels(CustomMultiValueAnnotation::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple("custom", "First"),
                Assertions.tuple("custom", "Second")
            )
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @LabelAnnotation(name = "custom", value = "fixed")
    internal annotation class CustomFixed

    @CustomFixed
    internal inner class CustomFixedAnnotation

    @Test
    fun shouldSupportCustomFixedAnnotations() {
        val labels = getLabels(CustomFixedAnnotation::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple("custom", "fixed")
            )
    }

    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @LabelAnnotations(
        LabelAnnotation(name = "a", value = "a1"),
        LabelAnnotation(name = "a", value = "a2"),
        LabelAnnotation(name = "b", value = "b1"),
        LabelAnnotation(name = "b", value = "b2")
    )
    internal annotation class CustomMultiLabel

    @CustomMultiLabel
    internal inner class CustomMultiLabelAnnotation

    /**
     * This test is ignored due to the fact that the call behaves differently in original implementation (Java 1.8)
     * and this implementation (Kotlin). [Class.getAnnotationsByType] doesn't return [LabelAnnotation]
     * for [CustomMultiLabel].
     */
    @Test
    @Disabled
    fun shouldSupportCustomMultiLabelAnnotations() {
        val labels = getLabels(CustomMultiLabelAnnotation::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple("a", "a1"),
                Assertions.tuple("a", "a2"),
                Assertions.tuple("b", "b1"),
                Assertions.tuple("b", "b2")
            )
    }

    @io.qameta.allure.kotlin.Link(name = "LINK-1")
    @Links(
        io.qameta.allure.kotlin.Link(
            name = "LINK-2",
            url = "https://example.org/link/2"
        ),
        io.qameta.allure.kotlin.Link(url = "https://example.org/some-custom-link")
    )
    @TmsLink("TMS-1")
    @TmsLinks(
        TmsLink("TMS-2"),
        TmsLink("TMS-3")
    )
    @Issue("ISSUE-1")
    @Issues(
        Issue("ISSUE-2"),
        Issue("ISSUE-3")
    )
    internal inner class WithLinks

    @ResourceLock(
        value = Resources.SYSTEM_PROPERTIES,
        mode = ResourceAccessMode.READ_WRITE
    )
    @SystemProperties(
        SystemProperty(name = "allure.link.issue.pattern", value = "https://example.org/issue/{}"),
        SystemProperty(name = "allure.link.tms.pattern", value = "https://example.org/tms/{}"),
        SystemProperty(name = "allure.link.custom.pattern", value = "https://example.org/custom/{}")
    )
    @Test
    fun shouldExtractLinks() {
        Assertions.assertThat(getLinks(WithLinks::class.java))
            .extracting(
                function(Link::name),
                function(Link::type),
                function(Link::url)
            )
            .contains(
                Assertions.tuple("LINK-1", "custom", "https://example.org/custom/LINK-1"),
                Assertions.tuple("LINK-2", "custom", "https://example.org/link/2"),
                Assertions.tuple("", "custom", "https://example.org/some-custom-link"),
                Assertions.tuple("ISSUE-1", "issue", "https://example.org/issue/ISSUE-1"),
                Assertions.tuple("ISSUE-2", "issue", "https://example.org/issue/ISSUE-2"),
                Assertions.tuple("ISSUE-3", "issue", "https://example.org/issue/ISSUE-3"),
                Assertions.tuple("TMS-1", "tms", "https://example.org/tms/TMS-1"),
                Assertions.tuple("TMS-2", "tms", "https://example.org/tms/TMS-2"),
                Assertions.tuple("TMS-3", "tms", "https://example.org/tms/TMS-3")
            )
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @LinkAnnotation
    internal annotation class CustomLink(val value: String = "")

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @LinkAnnotation(type = "issue")
    internal annotation class CustomIssue(val value: String)

    @CustomLink("LINK-2")
    @io.qameta.allure.kotlin.Link("LINK-1")
    @CustomIssue("ISSUE-1")
    internal inner class WithCustomLink

    @ResourceLock(
        value = Resources.SYSTEM_PROPERTIES,
        mode = ResourceAccessMode.READ_WRITE
    )
    @SystemProperties(
        SystemProperty(name = "allure.link.custom.pattern", value = "https://example.org/custom/{}"),
        SystemProperty(name = "allure.link.issue.pattern", value = "https://example.org/issue/{}")
    )
    @Test
    fun shouldExtractCustomLinks() {
        Assertions.assertThat(getLinks(WithCustomLink::class.java))
            .extracting<Any, Exception>(Link::url)
            .containsOnly(
                "https://example.org/custom/LINK-2",
                "https://example.org/custom/LINK-1",
                "https://example.org/issue/ISSUE-1"
            )
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @Feature("First")
    @Issue("1")
    internal annotation class FirstFeature

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @Feature("Second")
    @Issue("2")
    internal annotation class SecondFeature

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @FirstFeature
    @SecondFeature
    @Story("Other")
    internal annotation class OtherStory

    @OtherStory
    internal inner class WithMultiFeature

    @Test
    fun shouldSupportMultiFeature() {
        val labels = getLabels(WithMultiFeature::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple("feature", "First"),
                Assertions.tuple("feature", "Second"),
                Assertions.tuple("story", "Other")
            )
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS
    )
    @Recursive("second")
    @LabelAnnotation(name = "recursive")
    internal annotation class Recursive(val value: String)

    @Recursive("first")
    internal inner class WithRecurse

    @Test
    fun shouldExtractLabelsFromRecursiveAnnotations() {
        val labels = getLabels(WithRecurse::class.java)
        Assertions.assertThat(labels)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .contains(
                Assertions.tuple("recursive", "first"),
                Assertions.tuple("recursive", "second")
            )
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target
    internal annotation class Features {
        @Retention(AnnotationRetention.RUNTIME)
        @Target(
            AnnotationTarget.ANNOTATION_CLASS,
            AnnotationTarget.CLASS
        )
        @Feature("A")
        @Owner("tester1")
        @io.qameta.allure.kotlin.Link(url = "https://example.org/features/A")
        annotation class FeatureA {
            @Retention(AnnotationRetention.RUNTIME)
            @Target(
                AnnotationTarget.FUNCTION,
                AnnotationTarget.PROPERTY_GETTER,
                AnnotationTarget.PROPERTY_SETTER,
                AnnotationTarget.ANNOTATION_CLASS,
                AnnotationTarget.CLASS
            )
            @FeatureA
            @Story("s1")
            annotation class Story1
        }

        @Retention(AnnotationRetention.RUNTIME)
        @Target(
            AnnotationTarget.ANNOTATION_CLASS,
            AnnotationTarget.CLASS
        )
        @Feature("B")
        @Owner("tester2")
        @io.qameta.allure.kotlin.Link(url = "https://example.org/features/B")
        annotation class FeatureB {
            @Retention(AnnotationRetention.RUNTIME)
            @Target(
                AnnotationTarget.FUNCTION,
                AnnotationTarget.PROPERTY_GETTER,
                AnnotationTarget.PROPERTY_SETTER,
                AnnotationTarget.ANNOTATION_CLASS,
                AnnotationTarget.CLASS
            )
            @FeatureB
            @Story("s2")
            annotation class Story2
        }
    }

    @Story1
    internal inner class Test1

    @Story2
    internal inner class Test2

    @Story1
    @Story2
    internal inner class Test3

    @Test
    fun complexCase() {
        val labels1 = getLabels(Test1::class.java)
        Assertions.assertThat(labels1)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .containsExactlyInAnyOrder(
                Assertions.tuple("feature", "A"),
                Assertions.tuple("story", "s1"),
                Assertions.tuple("owner", "tester1")
            )
        val labels2 = getLabels(Test2::class.java)
        Assertions.assertThat(labels2)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .containsExactlyInAnyOrder(
                Assertions.tuple("feature", "B"),
                Assertions.tuple("story", "s2"),
                Assertions.tuple("owner", "tester2")
            )
        val labels3 = getLabels(Test3::class.java)
        Assertions.assertThat(labels3)
            .extracting(
                function(Label::name),
                function(Label::value)
            )
            .containsExactlyInAnyOrder(
                Assertions.tuple("feature", "A"),
                Assertions.tuple("story", "s1"),
                Assertions.tuple("owner", "tester1"),
                Assertions.tuple("feature", "B"),
                Assertions.tuple("story", "s2"),
                Assertions.tuple("owner", "tester2")
            )
    }
}

