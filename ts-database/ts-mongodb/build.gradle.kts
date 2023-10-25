plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-datastruct"))
  implementation(project(":ts-core:ts-log"))
  api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutineVersion)
  api(group = "org.mongodb", name = "mongodb-driver-reactivestreams", version = "4.4.0")
}




