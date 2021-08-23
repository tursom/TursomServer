import java.text.SimpleDateFormat
import java.util.*

plugins {
  kotlin("jvm") version "1.3.72"
  `java-gradle-plugin`
  `maven-publish`
}

try {
  val properties = Properties()
  properties.load(file("local.properties").inputStream())
  properties.forEach { (k, v) ->
    ext.set(k.toString(), v)
    try {
      setProperty(k.toString(), v)
    } catch (e: Exception) {
    }
  }
} catch (e: Exception) {
}

group = "com.ddbes"
version = SimpleDateFormat("yy.MM.dd-HH.mm").format(Date())

repositories {
  mavenCentral()
}

dependencies {
  implementation(group = "org.yaml", name = "snakeyaml", version = "1.29")
  implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
}

gradlePlugin {
  plugins {
    create("ddbes-gradle") {
      // 在 app 模块需要通过 id 引用这个插件
      id = "ddbes-gradle"
      // 实现这个插件的类的路径
      implementationClass = "DdbesGradlePlugin"
    }
  }
}

publishing {
  repositories {
    try {
      val artifactoryUser: String = rootProject.ext["tursom.artifactoryUser"] as String
      val artifactoryPassword: String = rootProject.ext["tursom.artifactoryPassword"] as String
      maven {
        val releasesRepoUrl = uri("https://nvm.tursom.cn/repository/maven-releases/")
        val snapshotRepoUrl = uri("https://nvm.tursom.cn/repository/maven-snapshots/")
        url = if (project.version.toString().endsWith("SNAPSHOT")) snapshotRepoUrl else releasesRepoUrl
        credentials {
          username = artifactoryUser
          password = artifactoryPassword
        }
      }
    } catch (e: Exception) {
      println("cannot push to repository tursom")
    }
    try {
      maven {
        val githubUser: String by rootProject
        val githubToken: String by rootProject
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/$githubUser/TursomServer")
        credentials {
          username = githubUser
          password = githubToken
        }
      }
    } catch (e: Exception) {
      println("cannot push to repository github")
    }

    val repositoriesRegex = "repositories\\.[a-zA-z]*".toRegex()
    rootProject.properties.keys.asSequence().filter {
      it matches repositoriesRegex
    }.forEach {
      val repositoryName = rootProject.ext.properties["$it.name"]?.toString() ?: it.substringAfterLast('.')
      try {
        val artifactoryUser = rootProject.ext.properties["$it.artifactoryUser"].toString()
        val artifactoryPassword = rootProject.ext.properties["$it.artifactoryPassword"].toString()
        maven {
          name = repositoryName
          val releasesRepoUrl = rootProject.ext.properties["$it.release"]?.let { uri(it.toString()) }
          val snapshotRepoUrl = rootProject.ext.properties["$it.snapshot"]?.let { uri(it.toString()) }
          val repoUrl = rootProject.ext.properties["$it.url"]?.let { uri(it.toString()) }
          url = if (project.version.toString().endsWith("SNAPSHOT")
            && snapshotRepoUrl != null
          ) {
            snapshotRepoUrl
          } else releasesRepoUrl ?: repoUrl!!
          credentials {
            username = artifactoryUser
            password = artifactoryPassword
          }
        }
      } catch (e: Exception) {
        println("cannot push to repository $repositoryName")
      }
    }
  }
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()

      from(components["java"])
      try {
        artifact(tasks["sourcesJar"])
      } catch (e: Exception) {
        try {
          artifact(tasks["kotlinSourcesJar"])
        } catch (e: Exception) {
        }
      }
    }
  }
}
