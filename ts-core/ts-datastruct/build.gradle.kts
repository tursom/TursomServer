plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-hash"))
}




