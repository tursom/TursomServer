plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  implementation(project(":"))
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
  compileOnly(group = "io.netty", name = "netty-all", version = "4.1.43.Final")
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
