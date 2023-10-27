package cn.tursom.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project

class InstallPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    if (!target.tasks.contains("install")) run install@{
      val publishToMavenLocal = target.tasks.findByName("publishToMavenLocal") ?: return@install
      target.tasks.register("install", DefaultTask::class.java) {
        it.finalizedBy(publishToMavenLocal)
      }
    }
  }
}

fun <T> NamedDomainObjectCollection<T>.contains(name: String) = try {
  findByName(name)
} catch (e: Exception) {
  null
} != null
