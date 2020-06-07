package io.qameta.allure.kotlin

import io.github.glytching.junit.extension.system.SystemProperty
import io.github.glytching.junit.extension.system.SystemPropertyExtension
import io.qameta.allure.kotlin.util.ResultsUtils.ISSUE_LINK_TYPE
import io.qameta.allure.kotlin.util.ResultsUtils.TMS_LINK_TYPE
import io.qameta.allure.kotlin.util.ResultsUtils.createIssueLink
import io.qameta.allure.kotlin.util.ResultsUtils.createLink
import io.qameta.allure.kotlin.util.ResultsUtils.createTmsLink
import io.qameta.allure.kotlin.util.ResultsUtils.getLinkTypePatternPropertyName
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(SystemPropertyExtension::class)
class ResultsUtilsTest {
    @Test
    fun shouldCreateLink() {
        val actual = createLink("a", "b", "c", "d")
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "a")
            .hasFieldOrPropertyWithValue("url", "c")
            .hasFieldOrPropertyWithValue("type", "d")
    }

    @Test
    fun shouldCreateLinkFromAnnotation() {
        val linkConstructor = Link::class.constructors
            .first { it.parameters.size == 4 }
            .call(
                "a_from_annotation",//value
                "b_from_annotation",//name
                "c_from_annotation",//url
                "d_from_annotation"//type
            )
        val actual = createLink(linkConstructor)
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "a_from_annotation")
            .hasFieldOrPropertyWithValue("url", "c_from_annotation")
            .hasFieldOrPropertyWithValue("type", "d_from_annotation")
    }

    @SystemProperty(name = "allure.link.issue.pattern", value = "https://example.org/issue/{}")
    @Test
    fun shouldCreateIssueLink() {
        val actual = createIssueLink("issue_link")
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "issue_link")
            .hasFieldOrPropertyWithValue("url", "https://example.org/issue/issue_link")
            .hasFieldOrPropertyWithValue("type", ISSUE_LINK_TYPE)
    }

    @SystemProperty(name = "allure.link.issue.pattern", value = "https://example.org/issue/{}")
    @Test
    fun shouldCreateIssueLinkFromAnnotation() {
        val actual = createLink(Issue::class.constructors.first().call("issue_link_from_annotation"))
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "issue_link_from_annotation")
            .hasFieldOrPropertyWithValue("url", "https://example.org/issue/issue_link_from_annotation")
            .hasFieldOrPropertyWithValue("type", ISSUE_LINK_TYPE)
    }

    @SystemProperty(name = "allure.link.tms.pattern", value = "https://example.org/tms/{}")
    @Test
    fun shouldCreateTmsLink() {
        val actual = createTmsLink("tms_link")
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "tms_link")
            .hasFieldOrPropertyWithValue("url", "https://example.org/tms/tms_link")
            .hasFieldOrPropertyWithValue("type", TMS_LINK_TYPE)
    }

    @SystemProperty(name = "allure.link.tms.pattern", value = "https://example.org/tms/{}")
    @Test
    fun shouldCreateTmsLinkFromAnnotation() {
        val actual = createLink(TmsLink::class.constructors.first().call("tms_link_from_annotation"))
        Assertions.assertThat(actual)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "tms_link_from_annotation")
            .hasFieldOrPropertyWithValue("url", "https://example.org/tms/tms_link_from_annotation")
            .hasFieldOrPropertyWithValue("type", TMS_LINK_TYPE)
    }


    @ParameterizedTest
    @MethodSource(value = ["data"])
    fun shouldCreateLink(
        value: String?,
        name: String?,
        url: String?,
        type: String?,
        sysProp: String?,
        expected: io.qameta.allure.kotlin.model.Link
    ) {
        setSystemProperty(type, sysProp)
        try {
            val actual = createLink(value, name, url, type)
            Assertions.assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", expected.name)
                .hasFieldOrPropertyWithValue("url", expected.url)
                .hasFieldOrPropertyWithValue("type", expected.type)
        } finally {
            clearSystemProperty(type, sysProp)
        }
    }

    private fun setSystemProperty(type: String?, sysProp: String?) {
        if (type != null && sysProp != null) {
            System.setProperty(getLinkTypePatternPropertyName(type), sysProp)
        }
    }

    private fun clearSystemProperty(type: String?, sysProp: String?) {
        if (type != null && sysProp != null) {
            System.clearProperty(getLinkTypePatternPropertyName(type))
        }
    }

    companion object {
        @JvmStatic
        fun data(): List<Arguments> = listOf(
            arrayOf("a", "b", "c", "d", "e", link("a", "c", "d")),
            arrayOf("a", "b", "c", "d", null, link("a", "c", "d")),
            arrayOf("a", "b", null, "d", "invalid-pattern", link("a", "invalid-pattern", "d")),
            arrayOf("a", "b", null, "d", "pattern/{}/some", link("a", "pattern/a/some", "d")),
            arrayOf(null, null, null, "d", "pattern/{}/some", link(null, "pattern//some", "d")),
            arrayOf(null, null, null, null, "pattern/{}/some", link(null, null, null)),
            arrayOf(null, "b", null, "d", "pattern/{}/some/{}/and-more", link("b", "pattern/b/some/b/and-more", "d")),
            arrayOf(null, "b", null, "d", null, link("b", null, "d"))
        ).map { values -> Arguments { values } }

        private fun link(name: String?, url: String?, type: String?): io.qameta.allure.kotlin.model.Link =
            io.qameta.allure.kotlin.model.Link(name = name, url = url, type = type)
    }
}