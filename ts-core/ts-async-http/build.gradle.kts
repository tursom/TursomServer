plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  api(project(":"))
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-xml"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  api(group = "com.squareup.retrofit2", name = "converter-gson", version = "2.9.0")
  // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
  api(group = "com.squareup.retrofit2", name = "retrofit", version = "2.9.0")

  // https://mvnrepository.com/artifact/org.jsoup/jsoup
  api(group = "org.jsoup", name = "jsoup", version = "1.13.1")


  testImplementation(project(":ts-core:ts-coroutine"))
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

tasks.register("install") {
  finalizedBy(tasks["publishToMavenLocal"])
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
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
