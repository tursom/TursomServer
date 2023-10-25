plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  api(project(":ts-core"))
  compileOnly(group = "io.netty", name = "netty-all", version = nettyVersion)
}




