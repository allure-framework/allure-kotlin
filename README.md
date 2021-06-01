[build]: https://github.com/allure-framework/allure-kotlin/actions
[build-badge]: https://github.com/allure-framework/allure-kotlin/workflows/Build/badge.svg
[release]: https://github.com/allure-framework/allure-kotlin/releases/latest "Latest release"
[release-badge]: https://img.shields.io/github/release/allure-framework/allure-kotlin.svg?style=flat
[maven]: https://repo.maven.apache.org/maven2/io/qameta/allure/allure-kotlin-android/ "Maven Central"
[maven-badge]: https://img.shields.io/maven-central/v/io.qameta.allure/allure-kotlin-android.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
[license-badge]: https://img.shields.io/badge/License-Apache%202.0-blue.svg

[![build-badge][]][build] [![release-badge][]][release] [![maven-badge][]][maven] [![License][license-badge]][license]

# Allure Kotlin Integrations

The repository contains Allure2 adaptors for JVM-based test frameworks targeting Kotlin and Java with 1.6 source compatibility. 

The core of this library was ported from `allure-java`. Thanks to that `allure-kotlin` has the same API, features, test coverage and solutions as `allure-java`. On top of the core library support for Kotlin and Android test frameworks were added.

Check out the [Allure Documentation][allure-docs].

## Supported frameworks
* JUnit4 
* Android Robolectric (via AndroidX Test)
* Android Instrumentation (via AndroidX Test)

## Getting started

### JUnit4

#### Setting up the dependency
```gradle
repositories {
    mavenCentral()
}

dependencies {
    testImplementation "io.qameta.allure:allure-kotlin-model:$LATEST_VERSION"
    testImplementation "io.qameta.allure:allure-kotlin-commons:$LATEST_VERSION"
    testImplementation "io.qameta.allure:allure-kotlin-junit4:$LATEST_VERSION"
}
```
#### Attaching listener

Attach the `AllureJunit4` run listener using one of available methods: 

- Attach the listener in build tool
    - **Maven** - use [Maven Surfire Plugin][maven-surfire-plugin]
    - **Gradle** - use [JUnit Foundation][junit-foundation] ([example][gradle-test-listener]) 
- Use predefined test runner 

```kotlin
@RunWith(AllureRunner::class)
class MyTest {
    ...
}

@RunWith(AllureParametrizedRunner::class)
class MyParameterizedTest {
    ...
}
```

### Android tests

#### Setting up the dependency
```gradle
repositories {
    mavenCentral()
}

dependencies {
    androidTestImplementation "io.qameta.allure:allure-kotlin-model:$LATEST_VERSION"
    androidTestImplementation "io.qameta.allure:allure-kotlin-commons:$LATEST_VERSION"
    androidTestImplementation "io.qameta.allure:allure-kotlin-junit4:$LATEST_VERSION"
    androidTestImplementation "io.qameta.allure:allure-kotlin-android:$LATEST_VERSION"
}
```

#### Attaching listener

AndroidX Test introduced a new `AndroidJUnit4` class runner that can be used for both **Robolectric** and **on-device instrumentation tests**. The same pattern is used for `AllureAndroidJUnit4` class runner. It attaches the allure listener to current class runner, but under the hood it uses `AndroidJUnit4`. All you need to do is to add `@RunWith(AllureAndroidJUnit4::class)` annotation to your test. 

```kotlin
@RunWith(AllureAndroidJUnit4::class)
class MyInstrumentationTest {
    ...
}
```

Using AllureAndroidJUnit4 over class - works for both robolectric and on-device tests.

#### Robolectric tests

Robolectric tests are simple unit tests, hence the API is the same. The report data will be placed in the same place as for unit tests. 

#### On-device instrumentation tests

You can also use testInstrumentationRunner for setting up runner.

```
android {
    defaultConfig {
        testInstrumentationRunner "io.qameta.allure.android.runners.AllureAndroidJUnitRunner"
    }
}
```

##### Integration
As on-device instrumentation test run on an actual device, the results have to be saved there as well. To do so permissions for accessing external storage are needed. If your app doesn't have those permissions, you can include them only in your debug build type (or any other build type under which the tests are executed):

**src/debug/AndroidManifest.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="io.qameta.allure.sample.junit4.android">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
</manifest>
```

Moreover, Allure will grant itself those permissions at runtime, so you don't have to place any special logic for Android 6.0+ devices. 

After the tests are finished you can pull the results from the external storage using an **adb** command like this one:
```
adb pull /sdcard/allure-results
```
Finally, you can generate the report via Allure CLI (see the [Allure Documentation][allure-cli]) or generate report with [allure-gradle][allure-gradle-plugin] plugin.


##### Features

The Allure Android API includes couple of features to make your reports a bit better.

###### Screenshot attachment

Screenshot can be taken and appended as an attachment to step or test in which they were executed:
```kotlin
@Test
fun screenshotExample() {
    step("Step screenshot") {
        allureScreenshot(name = "ss_step", quality = 90, scale = 1.0f)
    }
    allureScreenshot(name = "ss_test", quality = 50, scale = 1.0f)
}
```

###### Screenshot rule

Test rule to make the screenshot after each test and attach it to the test report. It includes a `mode` parameter which decides for which tests to make a screenshot:
* SUCCESS - only successful tests
* FAILURE - only failed tests
* END - all tests

```kotlin
@get:Rule
val logcatRule = ScreenshotRule(mode = ScreenshotRule.Mode.END, screenshotName = "ss_end")
```

###### Logcat rule

Test rule that clears the logcat before each test and appends the log dump as an attachment in case of failure.

```kotlin
@get:Rule
val logcatRule = LogcatRule()
```

###### Window hierarchy rule

You can use WindowHierarchyRule to capture a window hierarchy via uiautomator in case of Throwable during test.
```kotlin
@get:Rule
val windowHierarchyRule = WindowHierarchyRule()
```

## Samples

Different examples of `allure-kotlin` usage are presented in `samples` directory. This includes:
- `junit4-android` - complete Android sample with unit tests, robolectric tests and on device instrumentation tests

## Connection with allure-java

Following modules have been migrated:

* `allure-model` -> `allure-kotlin-model`
* `allure-java-commons` -> `allure-kotlin-commons`
* `allure-java-commons-test` -> `allure-kotlin-commons-test`

Following changes have to be made in order to keep the compatibility with Java 1.6: 
* `java.util.Optional` (Java 1.8+) -> Kotlin null type & safe call operators
* `java.util.stream.*` (Java 1.8+) -> Kotlin collection operators
* `java.nio.file.*` (Java 1.7+) -> migrating form `Path` to `File`
* repeatable annotations (Java 1.8+) -> no alternatives, feature not supported by JVM 1.6 

*The only part that was not migrated is aspects support.*

## Contributing

Thanks to all people who contributed. Especially [@kamildziadek](https://github.com/kamildziadek) who started allure-kotlin. [Contribute](.github/CONTRIBUTING.md).

## License

The Allure Framework is released under version 2.0 of the [Apache License][license].

[allure-gradle-plugin]: https://github.com/allure-framework/allure-gradle
[allure-cli]: https://docs.qameta.io/allure/#_reporting
[gradle-test-listener]: https://discuss.gradle.org/t/how-to-attach-a-runlistener-to-your-junit-4-tests-in-gradle/30788
[junit-foundation]: https://github.com/Nordstrom/JUnit-Foundation
[allure-docs]: https://docs.qameta.io/allure/
[maven-surfire-plugin]: https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html
