gradlePlugin {
  plugins {
    create("ts-gradle-no-test") {
      id = "ts-gradle-no-test"
      implementationClass = "cn.tursom.gradle.NoTestPlugin"
    }
  }
}
