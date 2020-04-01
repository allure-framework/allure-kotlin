description = "Allure Kotlin JUnit 4 Integration"

dependencies {
    api(project(":allure-kotlin-commons"))
    implementation("junit:junit:${Versions.junit4}")
    testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit5}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation(project(":allure-kotlin-commons-test"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}")
}

tasks.test {
    useJUnitPlatform()
}