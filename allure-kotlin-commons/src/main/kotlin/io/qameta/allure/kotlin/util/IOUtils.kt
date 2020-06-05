@file:JvmName("IOUtils")

package io.qameta.allure.kotlin.util

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

@Throws(IOException::class)
fun InputStream.toByteArray(): ByteArray =
    use { input ->
        ByteArrayOutputStream().use { output ->
            input.copyTo(output)
            output.toByteArray()
        }

    }