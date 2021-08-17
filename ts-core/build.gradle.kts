plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.29")
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
  compileOnly(group = "io.netty", name = "netty-all", version = "4.1.43.Final")
  testImplementation(group = "junit", name = "junit", version = "4.12")
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
