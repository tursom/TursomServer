gradlePlugin {
  plugins {
    create("ts-gradle-test") {
      id = "ts-gradle-test"
      implementationClass = "cn.tursom.gradle.TestPlugin"
    }
  }
}
