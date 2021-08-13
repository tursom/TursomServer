import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

ext["netty.version"] = "4.1.59.Final"
ext["excludeTest"] = { project: Project, tasks: TaskContainer ->
  if (project.gradle.startParameter.taskNames.firstOrNull { taskName ->
      ":test" in taskName
    } == null) {
    tasks {
      test { enabled = false }
      testClasses { enabled = false }
      compileTestJava { enabled = false }
      compileTestKotlin { enabled = false }
      processTestResources { enabled = false }
    }
  }
}
ext["publishRepositories"] = { project: Project, p: PublishingExtension ->
  val artifactoryUser: String by rootProject
  val artifactoryPassword: String by rootProject
  p.repositories {
    maven {
      val releasesRepoUrl = uri("https://nvm.tursom.cn/repository/maven-releases/")
      val snapshotRepoUrl = uri("https://nvm.tursom.cn/repository/maven-snapshots/")
      url = if (project.version.toString().endsWith("SNAPSHOT")) snapshotRepoUrl else releasesRepoUrl
      credentials {
        username = artifactoryUser
        password = artifactoryPassword
      }
    }
  }
}

try {
  val properties = Properties()
  properties.load(rootProject.file("local.properties").inputStream())
  properties.forEach { (k, v) ->
    rootProject.ext.set(k.toString(), v)
  }
} catch (e: Exception) {
}

plugins {
  kotlin("jvm") version "1.5.21"
  `maven-publish`
}

allprojects {
  group = "cn.tursom"
  version = "1.0"

  repositories {
    // mavenLocal()
    // mavenCentral()
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

@Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))
  testImplementation(group = "junit", name = "junit", version = "4.12")
}

artifacts {
  archives(tasks["kotlinSourcesJar"])
}

tasks.register("install") {
  finalizedBy(tasks["publishToMavenLocal"])
}

publishing {
  @Suppress("UNCHECKED_CAST")
  (rootProject.ext["publishRepositories"] as (Project, PublishingExtension) -> Unit)(project, this)
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()

      from(components["java"])
      try {
        artifact(tasks["kotlinSourcesJar"])
      } catch (e: Exception) {
      }
    }
  }
}
