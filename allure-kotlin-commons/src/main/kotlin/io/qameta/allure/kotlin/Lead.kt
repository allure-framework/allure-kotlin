package io.qameta.allure.kotlin

import io.qameta.allure.kotlin.util.ResultsUtils
import java.lang.annotation.Inherited

/**
 * This annotation used to specify project leads for test case.
 *
 * @see Owner
 */
@MustBeDocumented
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@LabelAnnotation(name = ResultsUtils.LEAD_LABEL_NAME)
annotation class Lead(val value: String)