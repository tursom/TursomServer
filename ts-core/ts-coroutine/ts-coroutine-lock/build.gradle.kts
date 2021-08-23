plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
}




