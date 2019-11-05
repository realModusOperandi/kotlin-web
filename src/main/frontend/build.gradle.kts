import com.moowork.gradle.node.npm.NpmTask
import net.wasdev.wlp.gradle.plugins.extensions.FeatureExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import net.wasdev.wlp.gradle.plugins.extensions.ServerExtension

plugins {
    war
    id("net.wasdev.wlp.gradle.plugins.Liberty").version("2.6.5")
    kotlin("jvm") version "1.3.50"
    id("com.moowork.node") version "1.3.1"
}

node {
    version = "12.9.1"
    npmVersion = "6.11.2"
    download = true
    workDir = file("$rootDir/src/main/frontend/node")
    nodeModulesDir = file("$rootDir/src/main/frontend")
}

tasks.register<Delete>("cleanClient") {
    delete(fileTree("$rootDir/src/main/webapp").matching {
        exclude("**/WEB-INF/**")
    })
}

tasks.register<Delete>("cleanNpm") {
    dependsOn("clean")
    delete("$rootDir/src/main/frontend/node", "$rootDir/src/main/frontend/node_modules")
}

tasks.register("npmUpdate") {
    dependsOn("npm_update")
}

tasks.register<NpmTask>("installDependencies") {
    dependsOn("npmSetup")
    setWorkingDir("$rootDir/src/main/frontend")
    setArgs(listOf("install"))
}

tasks.register<NpmTask>("buildStandaloneClient") {
    dependsOn("npmInstall")

    inputs.files(fileTree("${rootDir}/src/main/frontend/").matching {
        exclude("**/dist")
    }).withPropertyName("sourceFiles")
    outputs.dir("${rootDir}/src/main/frontend/dist").withPropertyName("outputDir")

    setWorkingDir("$rootDir/src/main/frontend")
    setArgs(listOf("run", "build"))
}

tasks.register<Copy>("copyFrontend") {
    dependsOn("cleanClient", "buildStandaloneClient")

    from(fileTree("$rootDir/src/main/frontend/build"))
    into("$rootDir/src/main/webapp")
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    providedCompile("javax:javaee-api:8.0")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    libertyRuntime("io.openliberty:openliberty-runtime:19.0.0.10")

}

val jmsPort by extra { 18000 }
val jmsSslPort by extra { 18001 }

liberty {
    server = ServerExtension("defaultServer")
    server.dropins = listOf(tasks["war"])
    server.features = FeatureExtension()
    server.features.name = listOf("microProfile-3.0")
    server.bootstrapProperties = mapOf("jmsPort" to jmsPort, "jmsSslPort" to jmsSslPort)
}

tasks["clean"].dependsOn(tasks["libertyStop"])
tasks["libertyStart"].dependsOn(tasks["libertyStop"])
tasks["libertyStart"].dependsOn("installFeature")
tasks["npm_update"].dependsOn("libertyStop")
tasks["war"].dependsOn("copyFrontend")

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}