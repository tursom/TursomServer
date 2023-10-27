dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-log"))
  api(project(":ts-web"))
  api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutineVersion)
  api(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.3")
  api(group = "io.netty", name = "netty-all", version = nettyVersion)
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
  testApi(group = "junit", name = "junit", version = "4.13.2")
  testApi(group = "com.squareup.okhttp3", name = "logging-interceptor", version = "4.9.3")
}




