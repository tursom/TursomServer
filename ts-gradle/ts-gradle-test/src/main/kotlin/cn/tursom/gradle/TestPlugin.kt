package cn.tursom.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class TestPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    if (target.isTestTask()) {
      target.tasks.withType(Test::class.java) {
        it.jvmArgs = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED")
      }
    } else {
      target.excludeTest()
    }
  }
}

fun Project.excludeTest() {
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

fun Project.isTestTask() = gradle.startParameter.taskNames.any { taskName ->
  taskName.contains("test", true)
}
