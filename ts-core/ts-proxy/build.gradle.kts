import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
  kotlin("plugin.allopen") version "1.5.21"
}

dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-reflectasm"))
  api(group = "cglib", name = "cglib", version = "3.3.0")
  implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.8.1")
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
}
