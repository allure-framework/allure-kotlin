package io.qameta.allure.kotlin

/**
 * Used to mark methods that produce attachments. Returned value of such methods
 * will be copied and shown in the report as attachment.
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class Attachment(
    /**
     * The attachment name.
     *
     * @return the attachment name.
     */
    val value: String = "",
    /**
     * Valid attachment MimeType, for example "text/plain" or "application/json".
     *
     * @return the attachment type.
     */
    val type: String = "",
    /**
     * Optional attachment file extension. By default file extension will be determined by
     * provided media type. Should be started with dot.
     *
     * @return the attachment file extension.
     */
    val fileExtension: String = ""
)