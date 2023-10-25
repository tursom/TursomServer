import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.0"
  id("ts-gradle")
}

allprojects {
  apply(plugin = "maven-publish")

  group = "cn.tursom"
  version = "1.1-SNAPSHOT"

  useTursomRepositories()

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    //kotlinOptions.useIR = true
  }

  if (!isTestRunning) {
    tasks.withType<Test> {
      enabled = false
    }
  }

  //autoConfigPublish()
}

dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
