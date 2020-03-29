package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.util.ResultsUtils
import java.lang.annotation.Inherited

/**
 * Used to mark test case with a story label.
 */
@MustBeDocumented
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS
)
/**
 * Due do compatibility with Java 1.6, Kotlin doesn't support repeatable annotations.
 * This annotation is still required in here, because of generic parsing of annotation containers
 * @see [AnnotationUtils.extractRepeatable]
 */
@Repeatable
@LabelAnnotation(name = ResultsUtils.STORY_LABEL_NAME)
annotation class Story(val value: String)