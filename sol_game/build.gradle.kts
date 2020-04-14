plugins {
    kotlin("jvm") version "1.3.61"
    application
    `maven-publish`
}


val loggerImplementation by configurations.creating

dependencies {


    implementation("org.java-websocket:Java-WebSocket:1.4.0")
    api(project(":sol_engine"))
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")

    implementation("io.github.microutils:kotlin-logging:1.7.7")
    loggerImplementation("org.slf4j:slf4j-simple:1.7.26")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
}

//java {
//    sourceCompatibility = JavaVersion.VERSION_11
//    targetCompatibility = JavaVersion.VERSION_11
//}

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

fun setRunSolProps(
        task: JavaExec,
        withArgs: List<String> = emptyList(),
        withJvmArgs: List<String> = emptyList(),
        debugLevel: String = "warn"
) {
    configurations["runtimeOnly"].extendsFrom(loggerImplementation)
    task.classpath = sourceSets["main"].runtimeClasspath
    task.main = "sol_game.Main"
    task.group = "application"
    task.args = withArgs
    task.jvmArgs = listOf(
            "-Dorg.lwjgl.util.Debug=false",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=$debugLevel"
    ) + withJvmArgs
}

tasks.register<JavaExec>("runPoolServer") {
    setRunSolProps(
            this,
            withArgs = listOf("--poolServer", "true"),
            withJvmArgs = listOf(),
            debugLevel = "warn"
    )
}

tasks.register<JavaExec>("runPoolServerHeadless") {
    setRunSolProps(
            this,
            withArgs = listOf("--poolServer", "headless"),
            withJvmArgs = listOf(),
            debugLevel = "warn"
    )
}

tasks.register<JavaExec>("runClient1") {
    setRunSolProps(
            this,
            withArgs = listOf("--client", "teamindex=0"),
            withJvmArgs = listOf(),
            debugLevel = "warn"
    )
}

tasks.register<JavaExec>("runClient2") {
    setRunSolProps(
            this,
            withArgs = listOf("--client", "teamindex=1"),
            withJvmArgs = listOf(),
            debugLevel = "warn"
    )
}

tasks.register<JavaExec>("runServerWithTwoClients") {
    setRunSolProps(
            this,
//            withArgs = listOf("--poolServer", "headless", "--client", "teamindex=0", "--client", "teamindex=1,headless"),
            withArgs = listOf("--poolServer", "true", "--client", "teamindex=0,headless", "--client", "teamindex=1,headless"),
            withJvmArgs = listOf(),
            debugLevel = "info"
    )
}

tasks.register<JavaExec>("runExhaustion") {
    setRunSolProps(
            this,
            withArgs = listOf("--runExhaustion"),
            withJvmArgs = listOf(),
            debugLevel = "error"
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