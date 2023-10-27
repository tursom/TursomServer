dependencies {
  implementation(project(":ts-gradle-env"))
}

gradlePlugin {
  plugins {
    create("ts-gradle-publish") {
      id = "ts-gradle-publish"
      implementationClass = "cn.tursom.gradle.PublishPlugin"
    }
  }
}
