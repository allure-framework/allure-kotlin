description = "Allure Kotlin Model Integration"

plugins {
    id("kotlinx-serialization") version Versions.kotlin
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kontlinxSerialization}")
}
