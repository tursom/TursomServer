plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-xml"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
  api(group = "com.squareup.retrofit2", name = "converter-gson", version = "2.9.0")
  // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
  api(group = "com.squareup.retrofit2", name = "retrofit", version = "2.9.0")

  // https://mvnrepository.com/artifact/org.jsoup/jsoup
  api(group = "org.jsoup", name = "jsoup", version = "1.14.2")


  testImplementation(project(":ts-core:ts-coroutine"))
}
