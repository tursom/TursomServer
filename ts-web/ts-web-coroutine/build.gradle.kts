plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  implementation(project(":"))
  implementation(project(":ts-web"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-json"))
  implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
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
