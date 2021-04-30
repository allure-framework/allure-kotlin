description = "Allure Kotlin Android Integration"

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    signing
}

apply(plugin = "com.github.dcendents.android-maven")

android {
    compileSdkVersion(Versions.Android.compileSdk)
    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        versionCode = 1
        versionName = version as String

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    api(project(":allure-kotlin-junit4"))
    implementation(kotlin("stdlib-jdk7"))
    implementation("androidx.test.ext:junit:${Versions.Android.Test.junit}")
    implementation("androidx.test:runner:${Versions.Android.Test.runner}")
    implementation("androidx.multidex:multidex:${Versions.Android.multiDex}")
    implementation("androidx.test.uiautomator:uiautomator:${Versions.Android.Test.uiAutomator}")
}

tasks.register<Javadoc>("androidJavadocs") {
    val androidLibrary = project.the(com.android.build.gradle.LibraryExtension::class)

    source(androidLibrary.sourceSets["main"].java.srcDirs)
    classpath += project.files(androidLibrary.bootClasspath.joinToString(File.pathSeparator))
    androidLibrary.libraryVariants.find { it.name == "release" }?.apply {
        classpath += javaCompileProvider.get().classpath
    }

    exclude("**/R.html", "**/R.*.html", "**/index.html")

    val stdOptions = options as StandardJavadocDocletOptions
    stdOptions.addBooleanOption("Xdoclint:-missing", true)
    stdOptions.links(
            "http://docs.oracle.com/javase/7/docs/api/",
            "http://developer.android.com/reference/",
            "http://hc.apache.org/httpcomponents-client-5.0.x/httpclient5/apidocs/",
            "http://hc.apache.org/httpcomponents-core-5.0.x/httpcore5/apidocs/")
}

tasks.register<Jar>("androidJavadocsJar") {
    val javadocTask = tasks.getByName<Javadoc>("androidJavadocs")
    dependsOn(javadocTask)
    archiveClassifier.set("javadoc")
    from(javadocTask.destinationDir)
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                artifact(tasks.getByName<Jar>("androidJavadocsJar"))
                artifact(tasks.getByName<Jar>("androidSourcesJar"))

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
}
