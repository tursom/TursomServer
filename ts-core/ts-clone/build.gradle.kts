plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-log"))
  implementation(project(":ts-core:ts-datastruct"))
}




