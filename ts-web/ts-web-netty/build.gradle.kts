dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-log"))
  api(project(":ts-web"))
  api(group = "io.netty", name = "netty-all", version = nettyVersion)
  api(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
}




