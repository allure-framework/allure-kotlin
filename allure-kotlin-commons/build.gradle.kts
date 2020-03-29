description = "Allure Kotlin Commons"

dependencies {
    api(project(":allure-kotlin-model"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kontlinxSerialization}")
    testImplementation("io.github.benas:random-beans:${Versions.randomBeans}")
    testImplementation("io.github.glytching:junit-extensions:${Versions.junitExtensions}")
    testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit5}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.junit5}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation(project(":allure-kotlin-commons-test"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}")
}

tasks.test {
    useJUnitPlatform()
}
