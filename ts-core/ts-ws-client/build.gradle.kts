plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-log"))
  compileOnly(project(":ts-socket"))
  api(group = "io.netty", name = "netty-all", version = "4.1.67.Final")
}



artifacts {
  archives(tasks["kotlinSourcesJar"])
}


