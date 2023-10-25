plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  implementation(project(":ts-core"))
  testImplementation(group = "junit", name = "junit", version = "4.13.2")
}




