plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.7")
  compileOnly(group = "io.netty", name = "netty-all", version = "4.1.67.Final")

  testApi(group = "junit", name = "junit", version = "4.13.2")
}




