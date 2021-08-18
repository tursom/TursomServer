plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  compileOnly(group = "org.springframework.data", name = "spring-data-mongodb", version = "3.2.4")
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
