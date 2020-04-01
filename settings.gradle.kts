pluginManagement {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }

    resolutionStrategy.eachPlugin {
        when (requested.id.id) {
            "org.jetbrains.kotlin.jvm" -> "org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}"
            "kotlinx-serialization" -> "org.jetbrains.kotlin:kotlin-serialization:${requested.version}"
            else -> null
        }?.let(::useModule)
    }

}
rootProject.name = "allure-kotlin"
include("allure-kotlin-model")
include("allure-kotlin-commons")
include("allure-kotlin-commons-test")
include("allure-kotlin-junit4")