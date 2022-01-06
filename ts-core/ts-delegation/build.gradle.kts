plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":ts-core"))
  compileOnly(group = "io.netty", name = "netty-all", version = "4.1.72.Final")
}




