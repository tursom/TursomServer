plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":"))
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

