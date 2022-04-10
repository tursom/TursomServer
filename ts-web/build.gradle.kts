plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-datastruct"))
  compileOnly(project(":ts-core:ts-coroutine"))
  compileOnly(project(":ts-core:ts-json"))
  compileOnly(group = "com.aayushatharva.brotli4j", name = "brotli4j", version = "1.7.1")
  implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
}
