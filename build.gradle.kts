plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    id("org.springframework.boot") version "2.5.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.severett"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    tasks {
        withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
            kotlinOptions {
                jvmTarget = "15"
                apiVersion = "1.4"
                languageVersion = "1.4"
            }
        }
        test {
            useJUnitPlatform()
        }
    }

    dependencies {
        val junitVersion: String by project

        // Production dependencies
        implementation("io.github.microutils:kotlin-logging-jvm:2.0.8")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
        implementation(kotlin("stdlib-jdk8"))
        // Test dependencies
        testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    }
}
