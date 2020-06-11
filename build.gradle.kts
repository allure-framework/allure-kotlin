plugins {
    java
    kotlin("jvm") version Versions.kotlin
}

allprojects {
    group = "io.qameta.allure"
    version = version

    repositories {
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        google()
        jcenter()
    }
}

val gradleScriptDir by extra("${rootProject.projectDir}/gradle")

configure(subprojects) {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(from = "$gradleScriptDir/maven-publish.gradle")
    apply(from = "$gradleScriptDir/github-publish.gradle")

    dependencies {
        implementation(kotlin("stdlib", Versions.kotlin))
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_6
        targetCompatibility = JavaVersion.VERSION_1_6
    }

    tasks.test {
        systemProperty("allure.model.indentOutput", "true")
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    tasks.processTestResources {
        filesMatching("**/allure.properties") {
            filter {
                it.replace("#project.description#", project.description ?: project.name)
            }
        }
    }
}