plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.9")
  compileOnly(group = "com.fasterxml.jackson.core", name = "jackson-core", version = "2.13.1")
  compileOnly(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.13.1")
}




