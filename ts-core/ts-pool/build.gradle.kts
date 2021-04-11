plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":ts-core:ts-buffer"))
    api(project(":ts-core:ts-datastruct"))
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

