plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "com.otakuworld"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.1.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    dependencies{
        // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
        implementation("com.squareup.okhttp3:okhttp:4.10.0-RC1")
        // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket
        implementation("org.java-websocket:Java-WebSocket:1.5.4")

        // https://mvnrepository.com/artifact/com.google.code.gson/gson
        implementation("com.google.code.gson:gson:2.10.1")

        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")

        // https://mvnrepository.com/artifact/org.projectlombok/lombok
        compileOnly("org.projectlombok:lombok:1.18.30")

        annotationProcessor("org.projectlombok:lombok:1.18.30")

        // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
        //testImplementation("org.slf4j:slf4j-simple:2.0.9")

        implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")

        //compileOnly("com.github.adedayo.intellij.sdk:git4idea:142.1")

        implementation("com.theokanning.openai-gpt3-java:service:0.16.0")

        implementation("org.kohsuke:github-api:1.315")
    }

}
configurations {
    all{
        exclude(group = "org.slf4j", module = "reload4j")
    }
}
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
}



sourceSets {
    main {
        resources {
            srcDirs("src/main/resources/images")
            srcDirs("src/main/resources/sounds")
        }
    }
}