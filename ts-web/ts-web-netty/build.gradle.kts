plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-log"))
  api(project(":ts-web"))
  api(group = "io.netty", name = "netty-all", version = "4.1.67.Final")
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
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
