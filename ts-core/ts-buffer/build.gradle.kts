plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core:ts-log"))
  implementation(project(":ts-core"))
  compileOnly(group = "io.netty", name = "netty-all", version = "4.1.72.Final")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
