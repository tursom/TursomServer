plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  implementation(project(":ts-web"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-coroutine"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-json"))
  implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutineVersion)
}




