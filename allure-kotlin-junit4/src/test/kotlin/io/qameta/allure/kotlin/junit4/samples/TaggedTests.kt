package io.qameta.allure.kotlin.junit4.samples

import io.qameta.allure.kotlin.junit4.Tag
import io.qameta.allure.kotlin.junit4.Tags
import org.junit.Test

@Tags(
    Tag(TaggedTests.CLASS_TAG1),
    Tag(TaggedTests.CLASS_TAG2)
)
class TaggedTests {
    @Test
    @Tags(
        Tag(METHOD_TAG1),
        Tag(METHOD_TAG2)
    )
    fun taggedTest() {
    }

    companion object {
        const val METHOD_TAG2 = "method_tag1"
        const val METHOD_TAG1 = "method_tag2"
        const val CLASS_TAG1 = "class_tag1"
        const val CLASS_TAG2 = "class_tag2"
    }
}