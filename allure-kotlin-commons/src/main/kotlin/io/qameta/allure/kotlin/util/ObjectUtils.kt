package io.qameta.allure.kotlin.util

import java.util.*
import java.util.logging.Logger

object ObjectUtils {
    private val LOGGER: Logger = loggerFor<ObjectUtils>()
    /**
     * Returns string representation of given object. Pretty prints arrays.
     *
     * @param data the given object.
     * @return the string representation of given object.
     */
    @JvmStatic
    fun toString(data: Any?): String {
        return try {
            if (data != null && data.javaClass.isArray) {
                when (data) {
                    is LongArray -> return data.contentToString()
                    is ShortArray -> return data.contentToString()
                    is IntArray -> return data.contentToString()
                    is CharArray -> return data.contentToString()
                    is DoubleArray -> return data.contentToString()
                    is FloatArray -> return data.contentToString()
                    is BooleanArray -> return data.contentToString()
                    is ByteArray -> return "<BINARY>"
                    is Array<*> -> return data.contentToString()
                }
            }
            Objects.toString(data)
        } catch (e: Exception) {
            LOGGER.error("Could not convert object to string", e)
            "<NPE>"
        }
    }
}