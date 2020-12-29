description = "Allure Kotlin Model Integration"

plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kontlinxSerialization}")
}
