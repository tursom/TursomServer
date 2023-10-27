gradlePlugin {
  plugins {
    create("ts-gradle-install") {
      id = "ts-gradle-install"
      implementationClass = "cn.tursom.gradle.InstallPlugin"
    }
  }
}
