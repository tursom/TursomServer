plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))

  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-clone"))
  implementation(project(":ts-core:ts-log"))

  // for support ExtSqlDialect
  compileOnly(project(":ts-core:ts-proxy"))
  compileOnly(project(":ts-core:ts-reflectasm"))

  api(group = "org.ktorm", name = "ktorm-core", version = "3.4.1")
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.9")

  testApi(group = "junit", name = "junit", version = "4.13.2")
  testApi(project(":ts-core:ts-proxy"))
  testApi(project(":ts-core:ts-reflectasm"))
  testApi(group = "org.xerial", name = "sqlite-jdbc", version = "3.36.0.3")
  testApi(group = "org.ktorm", name = "ktorm-support-sqlite", version = "3.4.1")
}




