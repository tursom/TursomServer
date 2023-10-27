dependencies {
  api(project(":"))
  api(project(":ts-core"))
  api(project(":ts-core:ts-buffer"))
  implementation(project(":ts-core:ts-xml"))
  api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutineVersion)
  api(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.3")
  compileOnly(group = "io.netty", name = "netty-all", version = nettyVersion)
  //api(group = "com.squareup.retrofit2", name = "converter-gson", version = "2.9.0")
  //api(group = "com.squareup.retrofit2", name = "retrofit", version = "2.9.0")

  // https://mvnrepository.com/artifact/org.jsoup/jsoup
  //api(group = "org.jsoup", name = "jsoup", version = "1.14.3")
  testImplementation(project(":ts-core:ts-coroutine"))
}
