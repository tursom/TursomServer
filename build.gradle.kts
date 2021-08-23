import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.21"
  `maven-publish`
  id("ts-gradle")
}

allprojects {
  group = "cn.tursom"
  version = "1.0-SNAPSHOT"

  repositories {
    mavenCentral()
    maven {
      url = uri("https://nvm.tursom.cn/repository/maven-public/")
    }
  }

  tasks.withType<JavaCompile> {
    tasks.withType<KotlinCompile>().configureEach {
      kotlinOptions.jvmTarget = "1.8"
      kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    if (project.gradle.startParameter.taskNames.firstOrNull { taskName ->
        taskName.endsWith(":test")
      } == null) {
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
}



dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))
  testApi(group = "junit", name = "junit", version = "4.13.2")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}
