package io.qameta.allure.junit4

import io.qameta.allure.LabelAnnotation
import io.qameta.allure.util.ResultsUtils.TAG_LABEL_NAME

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