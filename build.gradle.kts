import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  val kotlinVersion = "1.9.22"
  kotlin("jvm") version kotlinVersion
  kotlin("plugin.allopen") version kotlinVersion apply false

  id("ts-gradle-env") apply false
  id("ts-gradle-install") apply false
  id("ts-gradle-test") apply false
  id("ts-gradle-publish") apply false
  id("ts-gradle-repos") apply false
}

allprojects {
  group = "cn.tursom"
  version = "1.1-SNAPSHOT"

  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "maven-publish")
  apply(plugin = "ts-gradle-env")
  apply(plugin = "ts-gradle-install")
  apply(plugin = "ts-gradle-test")
  apply(plugin = "ts-gradle-publish")
  apply(plugin = "ts-gradle-repos")

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
  }

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
  }
}

dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
