plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  compile(project(":"))
  // https://mvnrepository.com/artifact/javax.mail/mail
  //compile group: "javax.mail", name: "mail", version: "1.4"
  // https://mvnrepository.com/artifact/com.sun.mail/javax.mail
  compile(group = "com.sun.mail", name = "javax.mail", version = "1.5.1")
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
