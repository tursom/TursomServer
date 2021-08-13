plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-clone"))
  implementation(project(":ts-core:ts-log"))
  api(group = "org.ktorm", name = "ktorm-core", version = "3.4.1")
  compileOnly(group = "com.baomidou", name = "mybatis-plus", version = "3.4.2")
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
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
