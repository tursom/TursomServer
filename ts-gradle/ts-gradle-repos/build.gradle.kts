import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

gradlePlugin {
  plugins {
    create("ts-gradle-repos") {
      id = "ts-gradle-repos"
      implementationClass = "cn.tursom.gradle.ReposPlugin"
    }
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}
