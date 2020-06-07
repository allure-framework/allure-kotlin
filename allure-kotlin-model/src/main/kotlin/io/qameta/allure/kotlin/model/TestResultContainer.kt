package io.qameta.allure.kotlin.model

import kotlinx.serialization.Serializable

/**
 * POJO that stores information about test fixtures.
 */
@Serializable
class TestResultContainer(
    val uuid: String,
    var name: String? = null,
    var description: String? = null,
    var descriptionHtml: String? = null,
    var start: Long? = null,
    var stop: Long? = null,
    val children: MutableList<String> = mutableListOf(),
    val befores: MutableList<FixtureResult> = mutableListOf(),
    val afters: MutableList<FixtureResult> = mutableListOf(),
    override val links: MutableList<Link> = mutableListOf()
) : WithLinks