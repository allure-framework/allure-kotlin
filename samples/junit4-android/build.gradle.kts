description = "Allure Kotlin Android Samples"

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdkVersion(Versions.Android.compileSdk)
    defaultConfig {
        applicationId = "io.qameta.allure.sample.junit4.android"
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        versionCode = 1
        versionName = version as String

        testInstrumentationRunner = "io.qameta.allure.android.runners.AllureAndroidJUnitRunner"
        testInstrumentationRunnerArguments(mapOf("clearPackageData" to "true"))
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        val sharedTestDir = "src/sharedTest/java"
        getByName("test").java.srcDir(sharedTestDir)
        getByName("androidTest").java.srcDir(sharedTestDir)
    }

    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))

    implementation("androidx.appcompat:appcompat:${Versions.Android.androidXappcompat}")
    implementation("androidx.core:core-ktx:${Versions.Android.androidXcore}")

    listOf(
            project(":allure-kotlin-android"),
            "androidx.test.ext:junit:${Versions.Android.Test.junit}",
            "androidx.test:runner:${Versions.Android.Test.runner}",
            "junit:junit:${Versions.junit4}",
            "androidx.test.espresso:espresso-core:${Versions.Android.Test.espresso}"
    ).forEach {
        testImplementation(it)
        androidTestImplementation(it)
    }

    testImplementation("org.robolectric:robolectric:${Versions.Android.Test.robolectric}")
}