plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
  kotlin("plugin.allopen") version "1.5.21"
}

dependencies {
  api(project(":ts-core"))
  implementation("cglib:cglib:3.3.0")
  implementation("org.apache.commons", "commons-lang3", "3.8.1")
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
