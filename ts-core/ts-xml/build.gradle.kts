plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  // 解析XML https://mvnrepository.com/artifact/org.dom4j/dom4j
  api(group = "org.dom4j", name = "dom4j", version = "2.1.3")
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
