package cn.tursom.gradle

import autoConfigPublish
import contains
import excludeTest
import ext
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException
import java.util.*

class TursomGradlePlugin : Plugin<Project> {
  companion object {
    val yaml = Yaml()
    var project: Project? = null
      private set
  }

  override fun apply(target: Project) {
    project = target
    listOf(
      "local.properties",
      "local.yml",
      "local.yaml",
      "gradle.yml",
      "gradle.yaml"
    ).forEach { propertiesFile ->
      when {
        propertiesFile.endsWith(".properties") -> loadProperties(target, propertiesFile)
        propertiesFile.endsWith(".yml") || propertiesFile.endsWith(".yaml") ->
          loadYaml(target, propertiesFile)
      }
    }
    //println(target.ext.properties)

    target.excludeTest()

    if (!target.tasks.contains("install")) run install@{
      val publishToMavenLocal = target.tasks.findByName("publishToMavenLocal") ?: return@install
      target.tasks.register("install", DefaultTask::class.java) {
        it.finalizedBy(publishToMavenLocal)
      }
    }

    target.autoConfigPublish()
  }
}


fun loadProperties(target: Project, propertiesFile: String) = try {
  val properties = Properties()
  properties.load(target.file(propertiesFile).inputStream())
  properties.forEach { (k, v) ->
    setProperty(target, k.toString(), v)
  }
} catch (e: Exception) {
}

fun loadYaml(target: Project, propertiesFile: String) {
  try {
    TursomGradlePlugin.yaml.load<Map<String, Any>>(target.file(propertiesFile).inputStream()).forEach { (k, v) ->
      put(target, k, v)
    }
  } catch (e: Exception) {
    if (e !is FileNotFoundException) {
      e.printStackTrace()
    }
  }
}

fun put(target: Project, key: String, value: Any?) {
  when (value) {
    null -> return
    is String, is Byte, is Short, is Int, is Long, is Float, is Double, is Char ->
      setProperty(target, key, value)

    else -> {
      setProperty(target, key, value)
      if (value is Map<*, *>) {
        value.forEach { (k, v) ->
          put(target, "$key.$k", v)
        }
      }
    }
  }
}

fun setProperty(target: Project, key: String, value: Any) {
  target.ext.set(key, value)
  try {
    target.setProperty(key, value)
  } catch (e: Exception) {
  }
}
