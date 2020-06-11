package io.qameta.allure

enum class SeverityLevel(val value: String) {
    BLOCKER("blocker"),
    CRITICAL("critical"),
    NORMAL("normal"),
    MINOR("minor"),
    TRIVIAL("trivial");

    override fun toString(): String = value
}