plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-buffer"))
}




