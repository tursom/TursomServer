plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-encrypt"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-pool"))
  implementation(project(":ts-core:ts-log"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
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
