plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-datastruct"))
}




