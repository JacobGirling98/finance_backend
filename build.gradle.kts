import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
    id("com.google.cloud.tools.jib") version "3.4.3"
}

group = "jacob.finance"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    implementation(platform("org.http4k:http4k-bom:4.32.2.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-client-apache")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-contract:4.39.0.0")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.3")
    implementation("ch.qos.logback:logback-classic:1.4.4")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.0")

    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation("com.google.api-client:google-api-client-jackson2:1.20.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    implementation("io.arrow-kt:arrow-core:1.2.4")

    testImplementation(kotlin("test"))
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.mockk:mockk:1.13.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("AppKt")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    reporters {
        reporter(ReporterType.HTML)
    }
}

jib {
    from {
        image = "openjdk:17-jdk-slim"
    }
    to {
        image = "finance-backend"
    }
    extraDirectories {
        paths {
            path {
                setFrom("src/main/resources/properties")
                into = "/app"
            }
        }
    }
    container {
        mainClass = "App.kt"
    }
}
