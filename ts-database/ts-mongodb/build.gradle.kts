plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-datastruct"))
  implementation(project(":ts-core:ts-log"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  api(group = "org.mongodb", name = "mongodb-driver-reactivestreams", version = "4.0.5")
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

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
        artifact(tasks["kotlinSourcesJar"])
      } catch (e: Exception) {
      }
    }
  }
}
