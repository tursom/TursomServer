dependencies {
  api(project(":"))
  implementation(project(":ts-core"))
  implementation(project(":ts-core:ts-clone"))
  compileOnly("org.mongodb:mongo-java-driver:3.12.10")
  compileOnly(group = "org.springframework.data", name = "spring-data-mongodb", version = "3.3.5")
  testApi(group = "junit", name = "junit", version = "4.13.2")
  testImplementation(group = "org.springframework.data", name = "spring-data-mongodb", version = "3.3.5")
}




