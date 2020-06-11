package io.qameta.allure

/**
 * Annotation that allows to attach a description for a test or for a step.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class Description(
    /**
     * Simple description text as String.
     *
     * @return Description text.
     */
    val value: String = "",
    /**
     * Use annotated method's javadoc to extract description that
     * supports html markdown.
     *
     * @return boolean flag to enable description extraction from javadoc.
     */
    val useJavaDoc: Boolean = false
)