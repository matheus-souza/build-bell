plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.13.3"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.sonarqube") version "4.4.1.3373"
}

sonar {
    properties {
        property("sonar.projectKey", "matheus-souza_build-bell")
        property("sonar.organization", "matheus-souza")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

group = "br.com.matheuhsouza"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
}

detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
}

intellij {
    plugins.set(listOf("Kotlin", "android"))
    version.set("2023.2.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    test {
        useJUnitPlatform()
    }

    runPluginVerifier {
        ideVersions.set(listOf("IC-2023.2", "IC-2024.1", "IC-2024.2", "IC-2025.1"))
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("263.*")
        changeNotes.set("Initial release of BuildBell.")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
