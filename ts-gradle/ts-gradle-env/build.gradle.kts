dependencies {
  implementation(group = "org.yaml", name = "snakeyaml", version = "2.0")
}

gradlePlugin {
  plugins {
    create("ts-gradle-env") {
      id = "ts-gradle-env"
      implementationClass = "cn.tursom.gradle.EnvPlugin"
    }
  }
}
