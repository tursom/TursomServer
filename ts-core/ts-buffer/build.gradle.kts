plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core:ts-log"))
  implementation(project(":ts-core"))
  compileOnly(group = "io.netty", name = "netty-all", version = nettyVersion)

  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
