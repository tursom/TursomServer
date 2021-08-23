plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  implementation(group = "com.sun.mail", name = "javax.mail", version = "1.6.2")
}




