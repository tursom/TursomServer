package cn.tursom.gradle

import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.util.concurrent.TimeUnit

class ReposPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.useTursomRepositories()
  }
}

fun Project.useTursomRepositories(
  useAliyunMirror: Boolean = false,
  mavenCentral: Boolean = false,
  tursom: Boolean = true
) {
  repositories.useTursomRepositories(useAliyunMirror, mavenCentral, tursom)
  try {
    configurations.noExpire()
  } catch (_: Exception) {
  }
}

context (Project)
fun RepositoryHandler.useTursomRepositories(
  useAliyunMirror: Boolean,
  mavenCentral: Boolean,
  tursom: Boolean
) {
  if (useAliyunMirror) {
    maven {
      it.url = project.uri("https://maven.aliyun.com/repository/public")
    }
  }
  if (mavenCentral) {
    mavenCentral()
  }
  if (tursom) {
    maven {
      it.url = project.uri("https://mvn.tursom.cn:20080/repository/maven-public")
    }
  }
}

fun DomainObjectCollection<Configuration>.noExpire() {
  all {
    it.resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    it.resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
  }
}
