import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("plugin.allopen")
}

dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-reflectasm"))
  api(group = "cglib", name = "cglib", version = "3.3.0")
  //implementation(group = "net.bytebuddy", name = "byte-buddy", version = "1.12.22")
  implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.8.1")
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
}

tasks.withType<Test>() {
  jvmArgs = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}
