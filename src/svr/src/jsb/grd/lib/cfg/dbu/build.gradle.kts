plugins {
    `java-library`
    id("io.spring.dependency-management")
}

group = "com.opor"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.2")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
