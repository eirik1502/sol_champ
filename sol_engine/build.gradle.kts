import org.gradle.internal.os.OperatingSystem

plugins {
    `java-library`
//	id 'application'

}

sourceSets {
    main {
        java {
            exclude("sol_engine/network_module/trash/**")
        }
    }
}

dependencies {
    // https://mvnrepository.com/artifact/org.reflections/reflections
    implementation(group = "org.reflections", name = "reflections", version = "0.9.11")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.github.java-json-tools:json-schema-validator:2.2.10")


    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.0")


    // https://mvnrepository.com/artifact/org.joml/joml
    api("org.joml:joml:1.9.14")

    //kryonet
    implementation("com.esotericsoftware:kryonet:2.22.0-RC1")

    // IMGUI + LWJGL
    val lwjgl_version = "3.2.3"
    val uno_version = "45be476e2cd87bec2ed0a9bf279b3cddc73ffcb9"
    val kotlin_version = "1.3.50"
    val glm_version = "3d3aea6d420bace96ced057cddeca023129d7c41"
    val imgui_version = "1.74-SNAPSHOT" //"e1fbe03a0a"

//    val lwjgl_version = "3.2.3"
//    val uno_version = "3275c3ce3045be19c84288609d9f1631d631a743"
//    val kotlin_version = "1.3.50"
//    val glm_version = "3d3aea6d420bace96ced057cddeca023129d7c41"
//    val imgui_version = "-SNAPSHOT" //"e1fbe03a0a"

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")

    //implementation("com.github.kotlin-graphics:imgui:1.73-SNAPSHOT")
    listOf("gl", "glfw", "core").forEach {
        api("com.github.kotlin-graphics.imgui:imgui-$it:$imgui_version")
    }

    implementation("com.github.kotlin-graphics:uno-sdk:$uno_version")
    api("com.github.kotlin-graphics.glm:glm:$glm_version")

    listOf("", "-glfw", "-opengl", "-stb").forEach {
        implementation("org.lwjgl:lwjgl$it:$lwjgl_version")
    }

    val lwjglNatives = when (OperatingSystem.current()) {
        OperatingSystem.WINDOWS -> "natives-windows"
        OperatingSystem.LINUX -> "natives-linux"
        OperatingSystem.MAC_OS -> "natives-macos"
        else -> ""
    }

    // Look up which modules and versions of LWJGL are required and add setup the approriate natives.
    val excludeLwjglLibs = listOf("lwjgl-jawt", "lwjgl-vulkan")
    configurations["compileClasspath"].resolvedConfiguration.getResolvedArtifacts()
            .stream()
            .filter {
                !((it.moduleVersion.id.group == "org.lwjgl") && (excludeLwjglLibs.contains(it.moduleVersion.id.name)))
            }
            .forEach {
                if (it.moduleVersion.id.group == "org.lwjgl") {
                    println("Loading $lwjglNatives for lib: $it")
                    runtime("org.lwjgl:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}:$lwjglNatives")
                }
            }

    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest:2.2")
}
