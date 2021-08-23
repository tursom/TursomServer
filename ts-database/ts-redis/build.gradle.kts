plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-log"))
  api(group = "redis.clients", name = "jedis", version = "3.6.3")
}




