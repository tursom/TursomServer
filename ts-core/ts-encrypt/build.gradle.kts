plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":ts-core:ts-buffer"))
    compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

