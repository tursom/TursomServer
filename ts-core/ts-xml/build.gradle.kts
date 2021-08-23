plugins {
  kotlin("jvm")
  `maven-publish`
  id("ts-gradle")
}

dependencies {
  api(project(":"))
  // 解析XML https://mvnrepository.com/artifact/org.dom4j/dom4j
  api(group = "org.dom4j", name = "dom4j", version = "2.1.3")
}




