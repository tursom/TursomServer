plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  implementation(project(":"))
  implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.29")
  implementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.3")
  implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
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
        artifact(tasks["sourcesJar"])
      } catch (e: Exception) {
      }
    }
  }
}