dependencies {
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  api(project(":ts-core:ts-log"))
  compileOnly(project(":ts-socket"))
  api(group = "io.netty", name = "netty-all", version = nettyVersion)
}



artifacts {
  archives(tasks["kotlinSourcesJar"])
}


