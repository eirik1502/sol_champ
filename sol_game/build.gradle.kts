plugins {
    kotlin("jvm") version "1.3.50"
    application
}

sourceSets {
    main {
        java {
            exclude("/**")
        }
    }
}

dependencies {
    implementation(project(":sol_engine"))
    implementation(kotlin("stdlib"))
}

//project.ext.set('nativeLibsDir', "$buildDir/libs/natives")

application {
    mainClassName = "sol_game.Main"
}

//
//run {
//
//    // standardInput = System.in
////    systemProperty 'java.library.path', project(':sol_engine').nativeLibsDir
//}