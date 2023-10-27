import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("plugin.allopen")
}

dependencies {
  api(project(":ts-core:ts-reflectasm"))
  implementation("org.apache.commons", "commons-lang3", "3.8.1")
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
}
