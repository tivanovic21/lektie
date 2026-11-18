// Shared code for both APIs: JPA entities, enums, DTOs.
plugins {
    `java-library`
    id("io.spring.dependency-management")
}

the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports { mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES) }
}

dependencies {
    api("jakarta.persistence:jakarta.persistence-api")
    api("jakarta.validation:jakarta.validation-api")
}
