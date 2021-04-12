plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  api(group = "me.liuwj.ktorm", name = "ktorm-core", version = "3.1.0")
  compileOnly(group = "com.baomidou", name = "mybatis-plus", version = "3.4.2")
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
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
