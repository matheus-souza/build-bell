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
    // Prevents IntelliJ from generating build/instrumented/instrumentCode/ — a second copy of
    // the plugin classes that could be loaded before our JaCoCo-instrumented version.
    instrumentCode.set(false)
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
    // pre-bake coverage probes into bytecode (offline instrumentation). Probes fire
    // regardless of how IntelliJ's PathClassLoader loads the class.
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
            val count = destDir.walk().filter { it.extension == "class" }.count()
            println(">>> [JaCoCo] Instrumented $count class files → ${destDir.absolutePath}")
        }
    }

    test {
        dependsOn(instrumentForCoverage)
        configure<JacocoTaskExtension> { isEnabled = false }
        systemProperty("jacoco-agent.destfile",
            layout.buildDirectory.file("jacoco/test.exec").get().asFile.absolutePath)
        // Classpath modification is in doFirst (execution time) so it runs AFTER IntelliJ
        // Gradle Plugin's afterEvaluate — ensuring our instrumented classes are truly first.
        doFirst {
            val testTask = this as org.gradle.api.tasks.testing.Test
            val instrumentedDir = project.layout.buildDirectory.dir("jacoco/instrumented-main")
            val runtimeConf = project.configurations["jacocoRuntime"]
            testTask.classpath = project.files(instrumentedDir) + runtimeConf + testTask.classpath

            println(">>> [JaCoCo] Test classpath entries: ${testTask.classpath.files.size}")
            println(">>> [JaCoCo] First 5 classpath entries:")
            testTask.classpath.files.take(5).forEach { println("    $it") }
            println(">>> [JaCoCo] Instrumented-main on classpath: ${testTask.classpath.files.any { it.path.contains("instrumented-main") }}")
            println(">>> [JaCoCo] JaCoCo runtime on classpath: ${testTask.classpath.files.any { it.name.contains("agent") && (it.name.contains("jacoco") || it.name.contains("runtime")) }}")
            println(">>> [JaCoCo] JVM args: ${testTask.jvmArgs}")
            println(">>> [JaCoCo] destfile: ${testTask.systemProperties["jacoco-agent.destfile"]}")
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
