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
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.0")
    //implementation 'com.google.code.gson:gson:2.8.5'
    implementation("com.google.code.gson:gson:2.8.5")

    implementation("com.github.java-json-tools:json-schema-validator:2.2.10")

    // https://mvnrepository.com/artifact/org.joml/joml
    api("org.joml:joml:1.9.14")

    //kryonet
    implementation("com.esotericsoftware:kryonet:2.22.0-RC1")

//    // IMGUI + LWJGL
    val lwjgl_version = "3.2.2"
    val uno_version = "3f32007ffe"
    val kotlin_version = "1.3.41"
    val glm_version = "6048c31425ae6110258e4b42165f1e636f8b5603"

    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")

//    compile 'com.github.kotlin-graphics:imgui:1.73-SNAPSHOT'
    listOf("gl", "glfw", "core").forEach {
        api("com.github.kotlin-graphics.imgui:imgui-$it:-SNAPSHOT")
    }

    implementation("com.github.kotlin-graphics:uno-sdk:$uno_version")
    implementation("com.github.kotlin-graphics.glm:glm:$glm_version")

    listOf("", "-glfw", "-opengl").forEach {
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
    configurations.compileClasspath.resolvedConfiguration.getResolvedArtifacts()
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

    testCompile("junit:junit:4.12")
}