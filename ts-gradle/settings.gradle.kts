include("ts-gradle-env")
include("ts-gradle-test")
include("ts-gradle-install")
include("ts-gradle-publish")
include("ts-gradle-repos")

pluginManagement {
  repositories {
    maven {
      url = uri("https://jmp.mvn.tursom.cn:20080/repository/maven-public/")
    }
  }
}
