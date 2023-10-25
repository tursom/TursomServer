plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutineVersion)
}




