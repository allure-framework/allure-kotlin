package io.qameta.allure.kotlin.junit4

import io.qameta.allure.kotlin.LabelAnnotation
import io.qameta.allure.kotlin.util.ResultsUtils.TAG_LABEL_NAME

@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Repeatable
@LabelAnnotation(name = TAG_LABEL_NAME)
annotation class Tag(val value: String)