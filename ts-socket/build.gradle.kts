plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-encrypt"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-pool"))
  implementation(project(":ts-core:ts-log"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
}




