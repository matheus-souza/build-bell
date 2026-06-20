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
    maybeCreate("jacocoAnt")
    maybeCreate("jacocoRuntime")
}

dependencies {
    testImplementation(kotlin("test"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    "jacocoAnt"("org.jacoco:org.jacoco.ant:${jacoco.toolVersion}")
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

    // JacocoInstrument is not a built-in Gradle task type. Use the JaCoCo Ant task to
    // pre-bake coverage probes into the bytecode before class loading (offline instrumentation).
    // This bypasses IntelliJ's PathClassLoader which uses Unsafe.defineClass() — a mechanism
    // that JaCoCo's on-the-fly ClassFileTransformer cannot intercept.
    val instrumentForCoverage by registering {
        dependsOn("compileKotlin")
        doLast {
            val classesDir = project.layout.buildDirectory.dir("classes/kotlin/main").get().asFile
            val destDir = project.layout.buildDirectory.dir("jacoco/instrumented-main").get().asFile
            destDir.mkdirs()
            project.ant.withGroovyBuilder {
                "taskdef"(
                    "name" to "jacocoInstrument",
                    "classname" to "org.jacoco.ant.InstrumentTask",
                    "classpath" to project.configurations["jacocoAnt"].asPath
                )
                "jacocoInstrument"("destdir" to destDir.absolutePath) {
                    "fileset"("dir" to classesDir.absolutePath, "includes" to "**/*.class")
                }
            }
        }
    }

    test {
        dependsOn(instrumentForCoverage)
        // Disable on-the-fly agent — offline-instrumented classes carry their own probes.
        configure<JacocoTaskExtension> { isEnabled = false }
        // Offline runtime reads this to know where to dump coverage at JVM shutdown.
        systemProperty("jacoco-agent.destfile",
            layout.buildDirectory.file("jacoco/test.exec").get().asFile.absolutePath)
        // Instrumented classes must precede originals on the classpath so PathClassLoader
        // (or any loader) picks up the probe-carrying version first.
        classpath = files(layout.buildDirectory.dir("jacoco/instrumented-main")) +
                configurations["jacocoRuntime"] + classpath
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
