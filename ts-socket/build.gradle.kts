plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-encrypt"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-pool"))
  api(project(":ts-core:ts-log"))
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

