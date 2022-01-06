plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  compileOnly(group = "org.springframework.data", name = "spring-data-mongodb", version = "3.3.0")
}




