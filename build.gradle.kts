import net.wasdev.wlp.gradle.plugins.extensions.FeatureExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import net.wasdev.wlp.gradle.plugins.extensions.ServerExtension

plugins {
    war
    id("net.wasdev.wlp.gradle.plugins.Liberty").version("2.6.3")
    kotlin("jvm") version "1.3.21"
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("javax:javaee-api:8.0")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    libertyRuntime("io.openliberty:openliberty-runtime:19.0.0.1")

}

liberty {
    server = ServerExtension("defaultServer")
    server.dropins = listOf(tasks["war"])
    server.features = FeatureExtension()
    server.features.name = listOf("microProfile-2.1")
}

tasks["clean"].dependsOn(tasks["libertyStop"])
tasks["libertyStart"].dependsOn(tasks["libertyStop"])

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}