plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core:ts-log"))
  implementation(project(":ts-core"))
  compileOnly(group = "io.netty", name = "netty-all", version = "4.1.72.Final")

  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
