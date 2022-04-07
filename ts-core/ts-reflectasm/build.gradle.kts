import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
  kotlin("plugin.allopen") version "1.5.21"
}

dependencies {
  api(project(":ts-core:ts-reflect"))
  api("com.esotericsoftware", "reflectasm", "1.11.9")
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
}
