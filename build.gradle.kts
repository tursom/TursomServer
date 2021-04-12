import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

ext["netty.version"] = "4.1.59.Final"
ext["excludeTest"] = { project: Project, tasks: TaskContainer ->
    if (project.gradle.startParameter.taskNames.firstOrNull { taskName ->
            taskName.endsWith(":test")
        } == null) {
        tasks {
            test { enabled = false }
            testClasses { enabled = false }
            compileTestJava { enabled = false }
            compileTestKotlin { enabled = false }
            processTestResources { enabled = false }
        }
    }
}


plugins {
    kotlin("jvm") version "1.4.32"
    `maven-publish`
}

allprojects {
    group = "cn.tursom"
    version = "0.2"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }

        if (project.gradle.startParameter.taskNames.firstOrNull { taskName ->
                taskName.endsWith(":test")
            } == null) {
            tasks.withType<Test> {
                enabled = false
            }
        }
    }
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

tasks.register("install") {
    finalizedBy(tasks["publishToMavenLocal"])
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            try {
                artifact(tasks["sourcesJar"])
            } catch (e: Exception) {
            }
        }
    }
}
