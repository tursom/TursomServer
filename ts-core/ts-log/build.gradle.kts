plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-delegation"))
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  api(group = "ch.qos.logback", name = "logback-core", version = "1.2.5")
  api(group = "ch.qos.logback", name = "logback-classic", version = "1.2.5")

  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.7")
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
