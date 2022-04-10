plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

val brotliVersion = "1.7.1"
val operatingSystem: OperatingSystem =
  org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()

dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-log"))
  api(project(":ts-web"))
  api(project(":ts-web:ts-web-netty"))
  api(project(":ts-core:ts-coroutine"))
  api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.6.0")
  api(group = "io.netty", name = "netty-all", version = "4.1.72.Final")
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  implementation(group = "io.netty", name = "netty-tcnative-boringssl-static", version = "2.0.46.Final")

  testApi(group = "junit", name = "junit", version = "4.13.2")
  testImplementation(group = "com.aayushatharva.brotli4j", name = "brotli4j", version = brotliVersion)
  testImplementation(
    group = "com.aayushatharva.brotli4j",
    name = "native-${
      if (operatingSystem.isWindows) "windows-x86_64"
      else if (operatingSystem.isMacOsX) "osx-x86_64"
      else if (operatingSystem.isLinux)
        if (org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentArchitecture().isArm) "linux-aarch64"
        else "native-linux-x86_64"
      else ""
    }",
    version = brotliVersion
  )
}




