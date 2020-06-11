# Allure Kotlin Integrations

The repository contains Allure2 adaptors for JVM-based test frameworks targeting Kotlin and Java with 1.6 source compatibility. 

The core of this library was ported from `allure-java`. Thanks to that `allure-kotlin` has the same API, features, test coverage and solutions as `allure-java`. On top of the core library support for Kotlin and Android test frameworks were added.

Check out the [Allure Documentation][allure-docs].

## Getting started

### JUnit4

#### Setting up the dependency
```gradle
repositories {
    maven { url 'https://dl.bintray.com/qameta/maven' }
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

Thanks to all people who contributed. Especially [@kamildziadek](https://github.com/kamildziadek) who started allure-kotlin. [[Contribute]](.github/CONTRIBUTING.md).

## License

The Allure Framework is released under version 2.0 of the [Apache License][license].

[gradle-test-listener]: https://discuss.gradle.org/t/how-to-attach-a-runlistener-to-your-junit-4-tests-in-gradle/30788
[junit-foundation]: https://github.com/Nordstrom/JUnit-Foundation
[allure-docs]: https://docs.qameta.io/allure/
[maven-surfire-plugin]: https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html
[license]: http://www.apache.org/licenses/LICENSE-2.0 "Apache License 2.0"