dependencies {
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-coroutine"))
  implementation(project(":ts-core:ts-encrypt"))
  implementation(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-pool"))
  implementation(project(":ts-core:ts-log"))
  implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutineVersion)
}




