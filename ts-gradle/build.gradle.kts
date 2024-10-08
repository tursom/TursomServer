//imextracted.text.SimpleDateFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
  kotlin("jvm") version "1.9.22" apply false
  `java-gradle-plugin`
}

try {
  val properties = Properties()
  properties.load(file("local.properties").inputStream())
  properties.forEach { (k, v) ->
    project.ext.set(k.toString(), v)
    try {
      setProperty(k.toString(), v)
    } catch (_: Exception) {
    }
  }
} catch (_: Exception) {
}

allprojects {
  group = "cn.tursom"
  version = "1.1-SNAPSHOT"

  apply(plugin = "maven-publish")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "java-gradle-plugin")

  repositories {
    maven {
      url = uri("https://mvn.tursom.cn:20080/repository/maven-public/")
    }
  }

  extensions.configure(PublishingExtension::class) {
    repositories {
      publishToTursom(this, this@allprojects)
      publishToGithub(this, this@allprojects)
      registerPublishRepos(this, this@allprojects)
    }
    publications {
      create<MavenPublication>("plugin") {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()

        from(components["java"])
        try {
          artifact(tasks["kotlinSourcesJar"])
        } catch (_: Exception) {
        }
      }
    }
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    //kotlinOptions.useIR = true
  }

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
  }
}

fun publishToTursom(repositoryHandler: RepositoryHandler, project: Project) {
  try {
    repositoryHandler.maven {
      name = "tursom"
      val releasesRepoUrl = project.uri("https://mvn.tursom.cn:20080/repository/maven-releases/")
      val snapshotRepoUrl = project.uri("https://mvn.tursom.cn:20080/repository/maven-snapshots/")
      url = if (project.project.version.toString().endsWith("SNAPSHOT")) snapshotRepoUrl else releasesRepoUrl
      credentials {
        val artifactoryUser: String = project.rootProject.ext["tursom.artifactoryUser"] as String
        val artifactoryPassword: String = project.rootProject.ext["tursom.artifactoryPassword"] as String
        username = artifactoryUser
        password = artifactoryPassword
      }
    }
  } catch (e: Exception) {
    println("cannot push to repository tursom: ${e.javaClass}: ${e.message}")
  }
}

fun publishToGithub(repositoryHandler: RepositoryHandler, project: Project) {
  try {
    repositoryHandler.maven {
      name = "GitHubPackages"
      val githubUser: String = project.rootProject.ext["github.artifactoryUser"] as String
      val githubToken: String = project.rootProject.ext["github.artifactoryPassword"] as String
      url = project.uri("https://maven.pkg.github.com/$githubUser/TursomServer")
      credentials {
        username = githubUser
        password = githubToken
      }
    }
  } catch (e: Exception) {
    println("cannot push to repository github")
  }
}

fun registerPublishRepos(repositoryHandler: RepositoryHandler, project: Project) {
  val repositoriesRegex = "repositories\\..*".toRegex()
  project.rootProject.properties.keys.asSequence().filter {
    it matches repositoriesRegex
  }.forEach {
    val repo = it.substringAfter(".").substringBefore('.')
    val repositoryName = project.rootProject.ext.properties["repositories.$repo.name"]?.toString() ?: repo
    try{
      repositoryHandler.getByName(repositoryName)
      return@forEach
    }catch (_: Exception) {
    }

    try {
      val artifactoryUser = project.rootProject.ext.properties["repositories.$repo.artifactoryUser"]?.toString()
      val artifactoryPassword = project.rootProject.ext.properties["repositories.$repo.artifactoryPassword"]?.toString()
      val token = project.rootProject.ext.properties["repositories.$repo.artifactoryToken"]?.toString()

      repositoryHandler.maven {
        name = repositoryName
        val releasesRepoUrl = project.rootProject.ext.properties["repositories.$repo.release"]?.let { project.uri(it.toString()) }
        val snapshotRepoUrl = project.rootProject.ext.properties["repositories.$repo.snapshot"]?.let { project.uri(it.toString()) }
        val repoUrl = project.rootProject.ext.properties["repositories.$repo.url"]?.let { project.uri(it.toString()) }
        url = if (project.project.version.toString().endsWith("SNAPSHOT")
          && snapshotRepoUrl != null
        ) {
          snapshotRepoUrl
        } else releasesRepoUrl ?: repoUrl!!

        if (artifactoryUser != null && artifactoryPassword != null) {
          credentials {
            username = artifactoryUser
            password = artifactoryPassword
          }
        } else if (token != null) {
          credentials(HttpHeaderCredentials::class.java) {
            name = "Authorization"
            value = "Bearer $token"
          }
          authentication {
            create<HttpHeaderAuthentication>("header")
          }
        }
      }
    } catch (e: Exception) {
      println("cannot push to repository $repositoryName")
    }
  }
}

val publishTask = tasks.getByName("publish")
subprojects.forEach {
  publishTask.dependsOn(it.tasks.getByName("publish").path)
}
