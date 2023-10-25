plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  compileOnly(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutineVersion)
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.9")
  compileOnly(group = "io.netty", name = "netty-all", version = nettyVersion)

  testApi(group = "junit", name = "junit", version = "4.13.2")
}
