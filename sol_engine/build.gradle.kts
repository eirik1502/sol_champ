plugins {
    `java-library`
    `maven-publish`
}

project.extra.set("nativesDir", "$buildDir/libs/natives")

sourceSets {
    main {
        java {
            exclude("sol_engine/archive/**")
        }
    }
    test {
        java {
            exclude("sol_engine/loaders/**", "sol_engine/ecs/**")
        }
    }
}

dependencies {
    // https://mvnrepository.com/artifact/org.reflections/reflections
    implementation(group = "org.reflections", name = "reflections", version = "0.9.11")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.github.java-json-tools:json-schema-validator:2.2.10")

    implementation("org.java-websocket:Java-WebSocket:1.4.0")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.0")

    // logging
    implementation("org.slf4j:slf4j-api:1.7.26")
//    runtimeOnly("org.slf4j:slf4j-simple:1.7.26")

    // https://mvnrepository.com/artifact/org.joml/joml
    api("org.joml:joml:1.9.14")

    //kryonet
//    implementation("com.esotericsoftware:kryonet:2.22.0-RC1")

    // IMGUI + LWJGL
    val lwjgl_version = "3.2.3"
    val uno_version = "c8a3099e8f5d335341df4010e8e7c20589317dfd"
    val kotlin_version = "1.3.61"
    val glm_version = "1b4ac18dd1a3c23440d3f33596688aac60bc0141"
    val imgui_version = "1.75"


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

    val lwjglNatives = "natives-windows"
//    val lwjglNatives = when (OperatingSystem.current()) {
//        OperatingSystem.WINDOWS -> "natives-windows"
//        OperatingSystem.LINUX -> "natives-linux"
//        OperatingSystem.MAC_OS -> "natives-macos"
//        else -> ""
//    }

    val natives by configurations.creating

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
                    natives("org.lwjgl:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}:$lwjglNatives")
                }
            }
    configurations["runtimeOnly"].extendsFrom(natives)

    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.register<Sync>("extractNatives") {
    from(configurations["natives"].map { zipTree(it) })
    into(file(project.extra.get("nativesDir") as String))
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

tasks.get("build").dependsOn("extractNatives")