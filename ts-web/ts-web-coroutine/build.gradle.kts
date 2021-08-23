plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  implementation(project(":ts-web"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-json"))
  implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
}




