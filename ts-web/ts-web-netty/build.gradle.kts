plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-log"))
  api(project(":ts-web"))
  api(group = "io.netty", name = "netty-all", version = "4.1.43.Final")
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.29")
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
