plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":ts-core"))
  compileOnly(group = "io.netty", name = "netty-all", version = "4.1.67.Final")
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
