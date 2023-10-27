package cn.tursom.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class NoTestPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.excludeTest()
  }
}

fun Project.excludeTest() {
  if (gradle.startParameter.taskNames.firstOrNull { taskName ->
      taskName.contains("test", true)
    } == null) {
    try {
      tasks.run {
        named("test") { it.enabled = false }
        named("testClasses") { it.enabled = false }
        named("compileTestJava") { it.enabled = false }
        try {
          named("compileTestKotlin") { it.enabled = false }
        } catch (_: Exception) {
        }
        named("processTestResources") { it.enabled = false }
      }
    } catch (e: Exception) {
      println("W: exclude tests failed with ${e.javaClass.name}: ${e.message}")
    }
  }
}
