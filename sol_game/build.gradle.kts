plugins {
    kotlin("jvm") version "1.3.61"
    application
    maven
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation(project(":sol_engine"))
    implementation(kotlin("stdlib"))
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
}


//
//run {
//
//    // standardInput = System.in
////    systemProperty 'java.library.path', project(':sol_engine').nativeLibsDir
//}