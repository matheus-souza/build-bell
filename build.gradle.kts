plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.17.4"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    jacoco
}

group = "br.com.matheuhsouza"
version = "1.0.0"

repositories {
    mavenCentral()
}

configurations {
    create("jacocoRuntime")
}

dependencies {
    testImplementation(kotlin("test"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    "jacocoRuntime"("org.jacoco:org.jacoco.agent:${jacoco.toolVersion}:runtime")
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
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    val instrumentForCoverage by registering(org.gradle.testing.jacoco.tasks.JacocoInstrument::class) {
        inputFiles.setFrom(sourceSets.main.get().output.classesDirs)
        outputDirectory.set(layout.buildDirectory.dir("jacoco/instrumented-main"))
    }

    test {
        dependsOn(instrumentForCoverage)
        // Offline instrumentation bakes JaCoCo probes into the bytecode before class loading,
        // bypassing IntelliJ's PathClassLoader which uses Unsafe.defineClass() — a method
        // that JaCoCo's on-the-fly ClassFileTransformer cannot intercept.
        configure<JacocoTaskExtension> { isEnabled = false }
        systemProperty("jacoco-agent.destfile",
            layout.buildDirectory.file("jacoco/test.exec").get().asFile.absolutePath)
        doFirst {
            // Apply at execution time so IntelliJ plugin's afterEvaluate classpath additions
            // are already in place; our instrumented classes must precede the originals.
            classpath = files(layout.buildDirectory.dir("jacoco/instrumented-main")) +
                    configurations["jacocoRuntime"] + classpath
        }
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)
        classDirectories.setFrom(sourceSets.main.get().output.classesDirs)
        executionData.setFrom(layout.buildDirectory.file("jacoco/test.exec"))
        reports {
            xml.required.set(true)
        }
    }

    runPluginVerifier {
        ideVersions.set(listOf("IC-2023.2", "IC-2024.1", "IC-2024.2", "IC-2025.1", "IC-2025.2"))
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
