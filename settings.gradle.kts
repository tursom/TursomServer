rootProject.name = "TursomServer"
includeBuild("ts-gradle")
include("ts-core")
include("ts-core:ts-encrypt")
include("ts-core:ts-buffer")
include("ts-core:ts-datastruct")
include("ts-core:ts-pool")
include("ts-core:ts-hash")
include("ts-core:ts-log")
include("ts-core:ts-curry")
include("ts-core:ts-delegation")
include("ts-core:ts-delegation:ts-observer")
include("ts-core:ts-clone")
include("ts-core:ts-mail")
include("ts-core:ts-coroutine")
include("ts-core:ts-coroutine:ts-coroutine-lock")
include("ts-core:ts-ws-client")
include("ts-core:ts-yaml")
include("ts-core:ts-json")
include("ts-core:ts-xml")
include("ts-core:ts-async-http")
include("ts-core:ts-proxy")
include("ts-core:ts-proxy-jdk")
include("ts-core:ts-reflect")
include("ts-core:ts-reflectasm")
include("ts-socket")
include("ts-web")
include("ts-web:ts-web-netty")
include("ts-web:ts-web-netty-client")
include("ts-web:ts-web-okhttp")
include("ts-web:ts-web-coroutine")
include("ts-database")
include("ts-database:ts-ktorm")
include("ts-database:ts-mybatisplus")
include("ts-database:ts-mongodb")
include("ts-database:ts-mongodb:ts-mongodb-spring")
include("ts-database:ts-redis")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      version("kotlin-version", "1.9.20-RC2")

      val kotlinCoroutineVersion = "1.7.3"
      version("kotlin-coroutines", kotlinCoroutineVersion)
      library(
        "kotlin-coroutines-core",
        "org.jetbrains.kotlinx",
        "kotlinx-coroutines-core"
      ).versionRef("kotlin-coroutines")
    }
  }
}

pluginManagement {
  repositories {
    maven {
      url = uri("https://jmp.mvn.tursom.cn:20080/repository/maven-public/")
    }
  }
}
