plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-delegation"))
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  api(group = "ch.qos.logback", name = "logback-core", version = "1.2.5")
  api(group = "ch.qos.logback", name = "logback-classic", version = "1.2.5")

  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.7")
}




