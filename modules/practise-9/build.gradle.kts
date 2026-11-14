plugins {
    application
    java
    id("org.springframework.boot")
    id("io.qameta.allure") version "4.3.0"
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
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    implementation(platform("org.springframework.ai:spring-ai-bom:2.0.1"))

    implementation("org.springframework.boot:spring-boot-starter:4.1.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator:4.1.1")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.1.1")
    implementation("org.springframework.boot:spring-boot-starter-validation:4.1.1")
    implementation("org.springframework.boot:spring-boot-starter-webmvc:4.1.1")
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
    implementation("org.springframework.boot:spring-boot-starter-liquibase:4.1.1")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test:4.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("io.qameta.allure:allure-junit5:2.32.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("e2e.base-url", System.getProperty("e2e.base-url", ""))
    systemProperty("e2e.model", System.getProperty("e2e.model", "qwen3:4b"))
}

allure {
    version = "3.16.1"
}

tasks.bootJar {
    archiveFileName = "practise-9.jar"
}

