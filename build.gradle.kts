plugins {
    java
    signing
    `maven-publish`

    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"

    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
}

buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}")
        classpath("com.android.tools.build:gradle:${Versions.Android.gradlePlugin}")
    }
}

val gradleScriptDir by extra("${rootProject.projectDir}/gradle")

nexusPublishing {
    repositories {
        sonatype()
    }
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

configure(subprojects
        .filter { !it.name.contains("android") }
        .filter { it.parent?.name != "samples" }
) {
    apply(plugin = "signing")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(kotlin("stdlib"))
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_6
        targetCompatibility = JavaVersion.VERSION_1_6
    }

    tasks.jar {
        manifest {
            attributes(mapOf(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version
            ))
        }
    }

    val sourceJar by tasks.creating(Jar::class) {
        from(sourceSets.getByName("main").allSource)
        archiveClassifier.set("sources")
    }

    val javadocJar by tasks.creating(Jar::class) {
        from(tasks.getByName("javadoc"))
        archiveClassifier.set("javadoc")
    }

    tasks.withType(Javadoc::class) {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifact(javadocJar)
                artifact(sourceJar)

                from(components["java"])

                pom {
                    name.set(project.name)
                    description.set("Module ${project.name} of Allure Framework.")
                    url.set("https://github.com/allure-framework/allure-kotlin")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("kamildziadek")
                            name.set("Kamil Dziadek")
                            email.set("kamildziadek0@gmail.com")
                        }
                        developer {
                            id.set("viclovsky")
                            name.set("Victor Orlovsky")
                            email.set("viclovsky@gmail.com")
                        }
                    }
                    scm {
                        developerConnection.set("scm:git:git://github.com/allure-framework/allure-kotlin")
                        connection.set("scm:git:git://github.com/allure-framework/allure-kotlin")
                        url.set("https://github.com/allure-framework/allure-kotlin")
                    }
                    issueManagement {
                        system.set("GitHub Issues")
                        url.set("hhttps://github.com/allure-framework/allure-kotlin/issue")
                    }
                }
            }

        }
    }

    signing {
        sign(publishing.publications["maven"])
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