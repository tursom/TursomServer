plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-datastruct"))
  implementation(project(":ts-core:ts-log"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
  api(group = "org.mongodb", name = "mongodb-driver-reactivestreams", version = "4.3.0")
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

tasks.register("install") {
  finalizedBy(tasks["publishToMavenLocal"])
}

publishing {
  @Suppress("UNCHECKED_CAST")
  (rootProject.ext["publishRepositories"] as (Project, PublishingExtension) -> Unit)(project, this)
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
