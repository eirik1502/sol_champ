plugins {
    kotlin("jvm") version "1.3.61"
    application
    `maven-publish`
}


dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.4.0")
    api(project(":sol_engine"))
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")

    implementation("io.github.microutils:kotlin-logging:1.7.7")
//    runtimeOnly("org.slf4j:slf4j-simple:1.7.26")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

//project.ext.set('nativeLibsDir', "$buildDir/libs/natives")
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        //        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11" //"1.8"
    }
}
application {
    mainClassName = "sol_game.Main"
    this.applicationDefaultJvmArgs = listOf(
            "-Dorg.lwjgl.util.Debug=true",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
    )
}

tasks.register<JavaExec>("runPoolServer") {
    classpath = sourceSets["main"].runtimeClasspath
    this.main = "sol_game.Main"
    this.group = "application"
    this.args = listOf("-P")
    this.jvmArgs = listOf(
            "-Dorg.lwjgl.util.Debug=false",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
    )
}
tasks.register<JavaExec>("runClient") {
    classpath = sourceSets["main"].runtimeClasspath
    this.main = "sol_game.Main"
    this.group = "application"
    this.args = listOf("-c")
    this.jvmArgs = listOf(
            "-Dorg.lwjgl.util.Debug=false",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"
    )
}

java {
    disableAutoTargetJvm()
}

publishing {
    publications {
        create<MavenPublication>("myLibrary") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "myRepo"
            url = uri("file://${projectDir}/../../solai_maven_repo")
        }
    }
}

//
//run {
//
//    // standardInput = System.in
////    systemProperty 'java.library.path', project(':sol_engine').nativeLibsDir
//}