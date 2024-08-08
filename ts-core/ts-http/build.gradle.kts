dependencies {
  api(project(":"))
  api(project(":ts-core"))
  api(group = "com.squareup.okhttp3", name = "okhttp", version = "4.12.0")
  compileOnly(group = "io.netty", name = "netty-all", version = nettyVersion)
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.9")
}
