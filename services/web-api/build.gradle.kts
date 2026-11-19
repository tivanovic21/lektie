// Web API: used by Lektie Studio (admins, teachers, content editors).
//
// Gradle in 30 seconds (compared to Maven):
// - services/settings.gradle.kts lists the modules (like <modules> in a parent pom).
// - services/build.gradle.kts holds what all modules share (Java 21, mavenCentral, JUnit), like a parent pom.
// - This file is this module's pom.xml. It is Kotlin code, so typos are compile errors in the IDE.
// - Run tasks with the wrapper from services/: ./gradlew :web-api:bootRun, :web-api:test, :web-api:bootJar.
//   ":web-api:" picks the module; without it the task runs in every module (./gradlew build).
// - The wrapper (gradlew + gradle/wrapper/) downloads the right Gradle version, so nobody installs Gradle.

plugins {
    // Versions come from services/build.gradle.kts ("apply false" there declares them once for all modules).
    id("org.springframework.boot")           // adds bootRun / bootJar tasks
    id("io.spring.dependency-management")    // imports the Spring Boot BOM, so starters need no version
}

// Maven scope -> Gradle configuration:
//   compile          -> implementation    (needed to compile and run)
//   runtime          -> runtimeOnly       (only on the classpath at run time, e.g. JDBC drivers)
//   test             -> testImplementation / testRuntimeOnly
dependencies {
    implementation(project(":common")) // another module of this build (shared entities)

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // JWT. Not in the Spring Boot BOM, so the version is written out.
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    runtimeOnly("org.postgresql:postgresql") // profile "postgres"
    runtimeOnly("com.h2database:h2")         // profile "h2" (in-memory, default for now)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Tasks can be configured too. "jar" would build a second, non-runnable *-plain.jar next to the
// runnable one from bootJar; turning it off leaves exactly one jar for the Dockerfile to copy.
tasks.jar {
    enabled = false
}
