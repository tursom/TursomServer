import org.gradle.api.DomainObjectCollection
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.internal.authentication.DefaultBasicAuthentication
import java.util.concurrent.TimeUnit

fun Project.excludeTest() {
  if (gradle.startParameter.taskNames.firstOrNull { taskName ->
      taskName.contains("test", true)
    } == null) {
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
  }
}

fun DomainObjectCollection<Configuration>.noExpire() {
  all {
    it.resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    it.resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
  }
}

fun Project.useTursomRepositories(
  useAliyunMirror: Boolean = false,
  mavenCentral: Boolean = false,
  tursom: Boolean = true
) {
  repositories.run {
    if (useAliyunMirror) {
      maven {
        it.url = uri("https://maven.aliyun.com/repository/public")
      }
    }
    if (mavenCentral) {
      mavenCentral()
    }
    if (tursom) {
      maven {
        it.url = uri("https://nvm.tursom.cn/repository/maven-public")
      }
    }
  }
  try {
    configurations.noExpire()
  } catch (_: Exception) {
  }
}

fun <T> NamedDomainObjectCollection<T>.contains(name: String) = try {
  findByName(name)
} catch (e: Exception) {
  null
} != null

operator fun Project.get(key: String) = ext[key]?.toString()

val Project.isTestRunning
  get() = gradle.startParameter.taskNames.firstOrNull { taskName ->
    taskName.endsWith(":test")
  } != null
