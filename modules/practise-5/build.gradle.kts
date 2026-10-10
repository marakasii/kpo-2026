plugins {
    application
    checkstyle
    java
    jacoco
    id("org.springframework.boot")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "studying.Main"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:4.1.1")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:4.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

// После тестов можно открыть HTML-отчёт и посмотреть покрытие своего кода.
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))
    reports {
        html.required = true
        xml.required = true
    }
}

// check завершится ошибкой, если покрытие упадёт ниже порогов практики 4.
tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.named("test"))
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.named("jacocoTestCoverageVerification"))
}

// Берём стандартные Sun Checks из той же версии Checkstyle, что в практике 4.
val sunChecks = configurations.detachedConfiguration(
    dependencies.create("com.puppycrawl.tools:checkstyle:14.1.0")
).apply {
    isTransitive = false
}

val checkstyleConfig = layout.buildDirectory.file("checkstyle/sun_checks.xml")

checkstyle {
    toolVersion = "14.1.0"
    config = resources.text.fromFile(checkstyleConfig.get().asFile)
    isShowViolations = true
    isIgnoreFailures = false
    maxWarnings = 0
}

val prepareCheckstyleConfig = tasks.register("prepareCheckstyleConfig") {
    inputs.files(sunChecks)
    outputs.file(checkstyleConfig)
    doLast {
        // Сохраняем те же два исключения из стандартных правил, что в практике 4.
        val standardConfig = resources.text
            .fromArchiveEntry(sunChecks, "sun_checks.xml")
            .asString()
        val configWithoutPackageInfoRule = standardConfig
            .replace("<module name=\"JavadocPackage\"/>", "")
            .replace("<module name=\"JavadocVariable\"/>", "")
            // README требует русские имена сценариев: разрешаем буквы Unicode.
            .replace(
                "<module name=\"MethodName\"/>",
                """
                <module name="MethodName">
                    <property name="format" value="^\p{Ll}[\p{L}\p{N}]*${'$'}"/>
                </module>
                """.trimIndent()
            )
        checkstyleConfig.get().asFile.apply {
            parentFile.mkdirs()
            writeText(configWithoutPackageInfoRule)
        }
    }
}

// Checkstyle проверяет и src/main/java, и src/test/java при запуске check.
tasks.withType<Checkstyle>().configureEach {
    dependsOn(prepareCheckstyleConfig)
    reports {
        html.required = true
        xml.required = true
    }
}
