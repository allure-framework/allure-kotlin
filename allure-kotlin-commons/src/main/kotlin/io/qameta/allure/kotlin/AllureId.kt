package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.util.ResultsUtils
import java.lang.annotation.Inherited

/**
 * Used by Allure Enterprise to link test cases with related test methods.
 */
@MustBeDocumented
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@LabelAnnotation(name = ResultsUtils.ALLURE_ID_LABEL_NAME)
annotation class AllureId(val value: String)