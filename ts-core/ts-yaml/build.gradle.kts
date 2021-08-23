plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  implementation(project(":ts-core"))
  // 解析YAML
  implementation(group = "org.yaml", name = "snakeyaml", version = "1.29")
}




