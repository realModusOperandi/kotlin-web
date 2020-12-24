import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.openliberty.tools.gradle.extensions.ServerExtension
import io.openliberty.tools.gradle.extensions.FeatureExtension


plugins {
    war
    id("io.openliberty.tools.gradle.Liberty") version "3.1.1"
    kotlin("jvm") version "1.4.21"
    id("com.github.node-gradle.node") version "2.2.4"
}

node {
    version = "14.15.3"
    npmVersion = "6.14.9"
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
    setWorkingDir(File("$rootDir/src/main/frontend"))
    setArgs(listOf("install"))
}

tasks.register<NpmTask>("buildStandaloneClient") {
    dependsOn("npmInstall")

    inputs.files(fileTree("${rootDir}/src/main/frontend/").matching {
        exclude("**/dist")
    }).withPropertyName("sourceFiles")
    outputs.dir("${rootDir}/src/main/frontend/dist").withPropertyName("outputDir")

    setWorkingDir(File("$rootDir/src/main/frontend"))
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
    providedCompile("org.eclipse.microprofile:microprofile:3.3")
    providedCompile("javax:javaee-api:8.0")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    libertyRuntime("io.openliberty:openliberty-runtime:20.0.0.12")
}

val jmsPort by extra { "18000" }
val jmsSslPort by extra { "18001" }

liberty {
    server = ServerExtension()
    server.name = "defaultServer"
    server.deploy.dropins = listOf(tasks["war"])
    server.features = FeatureExtension()
    server.features.name = listOf("microProfile-3.3")
    server.bootstrapProperties = mapOf("jmsPort" to jmsPort, "jmsSslPort" to jmsSslPort).toProperties()
}

tasks["clean"].dependsOn(tasks["libertyStop"])
tasks["libertyStart"].dependsOn(tasks["libertyStop"])
tasks["libertyStart"].dependsOn("installFeature")
tasks["npm_update"].dependsOn("libertyStop")
tasks["war"].dependsOn("copyFrontend")

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}