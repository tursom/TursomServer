import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

fun Project.autoConfigPublish() {
  try {
    extensions.configure(PublishingExtension::class.java, ::publish)
  } catch (e: Exception) {
    println("auto config publish failed: ${e.javaClass} ${e.message}")
  }
}

fun Project.publish(publish: PublishingExtension) {
  publish.repositories { repositoryHandler ->
    createTursomPublishRepository(repositoryHandler)
    scanAndCreatePublishRepository(repositoryHandler)
  }

  publish.publications(::createMavenPublications)
}

/**
 * create publish repository for tursom's server
 */
private fun Project.createTursomPublishRepository(repositoryHandler: RepositoryHandler) {
  try {
    repositoryHandler.maven { repository ->
      repository.name = "tursom"
      val version = getVersionWithProperties()
      if (warnVersionNotSet(version)) {
        return@maven
      }

      repository.url = if (version.endsWith("SNAPSHOT")) {
        uri("https://jmp.mvn.tursom.cn:20080/repository/maven-snapshots/")
      } else {
        uri("https://jmp.mvn.tursom.cn:20080/repository/maven-releases/")
      }

      repository.authentication{ ac ->
        ac.forEach {

        }
      }

      repository.credentials(PasswordCredentials::class.java) { credentials ->
        val artifactoryUser: String = rootProject.ext["tursom.artifactoryUser"]!!.toString()
        val artifactoryPassword: String = rootProject.ext["tursom.artifactoryPassword"]!!.toString()
        credentials.username = artifactoryUser
        credentials.password = artifactoryPassword
      }
    }
  } catch (e: Exception) {
    println("cannot publish to repository tursom:\n${e.javaClass}: ${e.message}")
  }
}

/**
 * scan properties begin with "publishRepositories" to create user defined publish repository
 */
private fun Project.scanAndCreatePublishRepository(repositoryHandler: RepositoryHandler) {
  val properties = rootProject.properties
  val repositoriesRegex = "^publishRepositories\\.[a-zA-z][a-zA-z0-9]*$".toRegex()

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

private fun Project.createMavenPublications(pc: PublicationContainer) {
  pc.maybeCreate("maven", MavenPublication::class.java).let { mavenPublication ->

    val groupId = project.group.toString()
      .ifBlank { ext.properties["project.groupId"]?.toString() }
    if (warnGroupIdNotSet(groupId)) {
      return
    }

    val version = getVersionWithProperties()
    if (warnVersionNotSet(version)) {
      return
    }

    mavenPublication.groupId = groupId
    mavenPublication.artifactId = project.name
    mavenPublication.version = version

    try {
      mavenPublication.from(components.getByName("java"))
    } catch (_: Exception) {
    }
    try {
      mavenPublication.artifact(tasks.getByName("kotlinSourcesJar"))
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}

/**
 * warn user that group id is not set.
 * @return true if group id is not set
 *         false if group id is set.
 */
private fun Project.warnGroupIdNotSet(groupId: String?) = if (groupId.isNullOrEmpty()) {
  println(
    "cannot publish to maven of project $this cause group id is not set. " +
      "you can specific property \"project.groupId\" to solve it."
  )
  true
} else {
  false
}

/**
 * warn user that group id is not set.
 * @return true if group id is not set
 *         false if group id is set.
 */
private fun Project.warnVersionNotSet(version: String?) = if (isEmptyVersion(version)) {
  println(
    "cannot publish to maven of project $this cause version is not set. " +
      "you can specific property \"project.version\" to solve it."
  )
  true
} else {
  false
}

private fun isEmptyVersion(version: String?) = version.isNullOrEmpty() || version == Project.DEFAULT_VERSION

private fun Project.getVersionWithProperties(): String {
  var version = this.version.toString()
  if (isEmptyVersion(version)) {
    version = ext["project.version"]?.toString() ?: Project.DEFAULT_VERSION
  }

  return version
}