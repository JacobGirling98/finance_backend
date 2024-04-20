import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
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

    implementation("org.eclipse.jgit:org.eclipse.jgit:6.3.0.202209071007-r")

    implementation("org.mongodb:mongodb-driver-sync:4.9.1")

    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation("com.google.api-client:google-api-client-jackson2:1.20.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    testImplementation(kotlin("test"))
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.mockk:mockk:1.13.5")
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("AppKt")
}

// val fatJar = task("fatJar", type = Jar::class) {
//    manifest {
//        attributes["Main-Class"] = "AppKt"
//    }
//    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
//    with(tasks.jar.get() as CopySpec)
// }

// tasks {
//    "build" {
//        dependsOn(fatJar)
//    }
// }

tasks.withType<Jar> {

    manifest {
        attributes["Main-Class"] = "AppKt"
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    reporters {
        reporter(ReporterType.HTML)
    }
}
