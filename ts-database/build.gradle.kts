dependencies {
  api(kotlin("stdlib-jdk8"))
  api(kotlin("reflect"))

  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-clone"))
  implementation(project(":ts-core:ts-log"))
  api(group = "org.ktorm", name = "ktorm-core", version = "3.4.1")
  compileOnly(group = "com.baomidou", name = "mybatis-plus", version = "3.5.3.2")
  compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.9")

  testApi(group = "junit", name = "junit", version = "4.13.2")
}




