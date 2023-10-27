dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-datastruct"))
  implementation(project(":ts-core:ts-log"))
  api(libs.kotlin.coroutines.core)
  api(group = "org.mongodb", name = "mongodb-driver-reactivestreams", version = "4.4.0")
}




