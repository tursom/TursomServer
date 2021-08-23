plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-clone"))
  implementation(project(":ts-core:ts-log"))
  api(group = "org.ktorm", name = "ktorm-core", version = "3.4.1")
  compileOnly(group = "com.baomidou", name = "mybatis-plus", version = "3.4.3.1")
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.7")
}




