package io.qameta.allure

import io.qameta.allure.util.ResultsUtils
import java.lang.annotation.Inherited

/**
 * Use this annotation to add some links to results. Usage:
 * <pre>
 * &#064;Link("https://qameta.io")
 * public void myTest() {
 * ...
 * }
</pre> *
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
@LinkAnnotation
/**
 * Due do compatibility with Java 1.6, Kotlin doesn't support repeatable annotations.
 * This annotation is still required in here, because of generic parsing of annotation containers
 * @see [AnnotationUtils.extractRepeatable]
 */
@Repeatable
annotation class Link(
    /**
     * Alias for [.name].
     *
     * @return the link name.
     */
    val value: String = "",
    /**
     * Name for link, by default url.
     *
     * @return the link name.
     */
    val name: String = "",
    /**
     * Url for link. By default will search for system property `allure.link.{type}.pattern`, and use it
     * to generate url.
     *
     * @return the link url.
     */
    val url: String = "",
    /**
     * This type is used for create an icon for link. Also there is few reserved types such as issue and tms.
     *
     * @return the link type.
     */
    val type: String = ResultsUtils.CUSTOM_LINK_TYPE
)