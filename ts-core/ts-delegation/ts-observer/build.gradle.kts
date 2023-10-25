plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  api(project(":ts-core:ts-delegation"))
}




