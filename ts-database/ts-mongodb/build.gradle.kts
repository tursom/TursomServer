plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-datastruct"))
  implementation(project(":ts-core:ts-log"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
  api(group = "org.mongodb", name = "mongodb-driver-reactivestreams", version = "4.3.0")
}




