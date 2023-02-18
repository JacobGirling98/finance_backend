import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "jacob.finance"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
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
    testImplementation(kotlin("test"))
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.3")
    testImplementation("io.kotest:kotest-assertions-core:5.5.3")
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

//val fatJar = task("fatJar", type = Jar::class) {
//    manifest {
//        attributes["Main-Class"] = "AppKt"
//    }
//    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
//    with(tasks.jar.get() as CopySpec)
//}

//tasks {
//    "build" {
//        dependsOn(fatJar)
//    }
//}

tasks.withType<Jar> {

    manifest {
        attributes["Main-Class"] = "AppKt"
    }


}