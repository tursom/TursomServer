plugins {
  kotlin("jvm")
  id("ts-gradle")
}

dependencies {
  implementation(project(":"))
  api(group = "com.sun.mail", name = "javax.mail", version = "1.6.2")
}




