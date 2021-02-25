description = "Allure Kotlin Android Integration"

plugins {
    id("com.android.library")
    kotlin("android")
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


val sourcesJar =  tasks.register<Jar>(name = "sourceJar") {
    from(android.sourceSets["main"].java.srcDirs)
    classifier = "sources"
}

val javadocJar = tasks.register<Jar>(name = "javadocJar") {
    archiveClassifier.set("javadoc")
    from("javadoc")
}

artifacts {
    add("archives", javadocJar)
    add("archives", sourcesJar)
}

dependencies {
    api(project(":allure-kotlin-junit4"))
    implementation(kotlin("stdlib-jdk7"))
    implementation("androidx.test.ext:junit:${Versions.Android.Test.junit}")
    implementation("androidx.test:runner:${Versions.Android.Test.runner}")
    implementation("androidx.multidex:multidex:${Versions.Android.multiDex}")
    implementation("androidx.test.uiautomator:uiautomator:${Versions.Android.Test.uiAutomator}")
}
