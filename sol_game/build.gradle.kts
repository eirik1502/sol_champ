plugins {
    kotlin("jvm") version "1.3.61"
    application
    `maven-publish`
}

sourceSets {
    main {
        java {
            exclude("/**")
        }
    }
}

dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.4.0")
    implementation(project(":sol_engine"))
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")

    implementation("io.github.microutils:kotlin-logging:1.7.7")
    implementation("org.slf4j:slf4j-simple:1.7.26")
}

//project.ext.set('nativeLibsDir', "$buildDir/libs/natives")
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        //        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
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
    this.args = listOf("poolServer")
    this.jvmArgs = listOf(
            "-Dorg.lwjgl.util.Debug=false",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
    )
}
tasks.register<JavaExec>("runClient") {
    classpath = sourceSets["main"].runtimeClasspath
    this.main = "sol_game.Main"
    this.group = "application"
    this.args = listOf("client")
    this.jvmArgs = listOf(
            "-Dorg.lwjgl.util.Debug=false",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
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