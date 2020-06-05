package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.util.ResultsUtils
import java.lang.annotation.Inherited

/**
 * Used to set severity for tests.
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
@LabelAnnotation(name = ResultsUtils.SEVERITY_LABEL_NAME)
annotation class Severity(val value: SeverityLevel)