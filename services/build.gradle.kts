plugins {
    id("org.springframework.boot") version "4.0.9" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
    group = "hr.lektie"
    version = "0.1.0-SNAPSHOT"

    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
