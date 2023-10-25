plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-pool"))
  implementation(project(":ts-core:ts-datastruct"))

  testApi(group = "com.google.code.gson", name = "gson", version = "2.8.9")
  testApi(group = "junit", name = "junit", version = "4.13.2")
}




