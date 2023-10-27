import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("plugin.allopen")
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
