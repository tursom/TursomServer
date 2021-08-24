import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.util.concurrent.TimeUnit

var nettyVersion = "4.1.67.Final"

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
      } catch (e: Exception) {
      }
      named("processTestResources") { it.enabled = false }
    }
  }
}

fun Project.publish(publish: PublishingExtension) {
  val properties = rootProject.properties
  publish.repositories { repositoryHandler ->
    try {
      repositoryHandler.maven { repository ->
        repository.name = "tursom"
        repository.url = if (version.toString().endsWith("SNAPSHOT")) {
          uri("https://nvm.tursom.cn/repository/maven-snapshots/")
        } else {
          uri("https://nvm.tursom.cn/repository/maven-releases/")
        }
        repository.credentials { credentials ->
          val artifactoryUser: String = rootProject.ext["tursom.artifactoryUser"]!!.toString()
          val artifactoryPassword: String = rootProject.ext["tursom.artifactoryPassword"]!!.toString()
          credentials.username = artifactoryUser
          credentials.password = artifactoryPassword
        }
      }
    } catch (e: Exception) {
      println("cannot publish to repository tursom:\n${e.javaClass}: ${e.message}")
    }

    val repositoriesRegex = "publishRepositories\\.[a-zA-z][a-zA-z0-9]*".toRegex()
    properties.keys.asSequence().filter {
      it matches repositoriesRegex
    }.forEach { repositoryName ->
      try {
        val artifactoryUser = rootProject.ext["$repositoryName.artifactoryUser"]?.toString()
          ?: throw Exception("no artifactory user found")
        val artifactoryPassword = rootProject.ext["$repositoryName.artifactoryPassword"]?.toString()
          ?: throw Exception("no artifactory password found")
        repositoryHandler.maven { repository ->
          repository.name = properties["$repository.name"]?.toString()
            ?: repositoryName.substringAfterLast('.')
          val releasesRepoUrl = properties["$repositoryName.release"]?.let {
            uri(it.toString())
          }
          val snapshotRepoUrl = properties["$repositoryName.snapshot"]?.let {
            uri(it.toString())
          }
          val repoUrl = properties["$repositoryName.url"]?.let {
            uri(it.toString())
          }
          repository.url = if (version.toString().endsWith("SNAPSHOT") && snapshotRepoUrl != null) {
            snapshotRepoUrl
          } else {
            releasesRepoUrl
          } ?: repoUrl ?: throw Exception("no repo found")
          repository.credentials {
            it.username = artifactoryUser
            it.password = artifactoryPassword
          }
        }
      } catch (e: Exception) {
        println(
          "cannot publish to repository ${repositoryName.substringAfterLast('.')}:\n" +
            "${e.javaClass}: ${e.message}"
        )
      }
    }
  }
  publish.publications {
    it.create("maven", MavenPublication::class.java) { mavenPublication ->
      mavenPublication.groupId = project.group.toString()
      mavenPublication.artifactId = project.name
      mavenPublication.version = project.version.toString()

      try {
        mavenPublication.from(components.getByName("java"))
      } catch (e: Exception) {
      }
      try {
        mavenPublication.artifact(tasks.getByName("kotlinSourcesJar"))
      } catch (e: Exception) {
      }
    }
  }
}

fun DomainObjectCollection<Configuration>.noExpire() {
  all {
    it.resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    it.resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
  }
}

fun Project.userTursomRepositories(
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
  } catch (e: Exception) {
  }
}
