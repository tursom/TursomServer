import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.0"
  `maven-publish`
  id("ts-gradle")
}

allprojects {
  group = "cn.tursom"
  version = "1.0-SNAPSHOT"

  useTursomRepositories()

  tasks.withType<JavaCompile> {
    tasks.withType<KotlinCompile>().configureEach {
      kotlinOptions.jvmTarget = "1.8"
      kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    if (!isTestRunning) {
      tasks.withType<Test> {
        enabled = false
      }
    }
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    //kotlinOptions.useIR = true
  }

  autoConfigPublish()
}



dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
