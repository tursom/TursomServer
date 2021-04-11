dependencies {
  implementation(project(":"))
  implementation(project(":utils"))
  api project (":utils:xml")

  // kotlin 协程
  compile 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2'
  // kotlin 反射
  //implementation "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
  // OkHttp
  //implementation("com.squareup.okhttp3:okhttp:3.14.1")
  //implementation group: 'cglib', name: 'cglib', version: '3.3.0'
  // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
  api group : 'com.squareup.retrofit2', name: 'converter-gson', version: '2.9.0'
  // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
  compile group : 'com.squareup.retrofit2', name: 'retrofit', version: '2.9.0'

  // https://mvnrepository.com/artifact/org.jsoup/jsoup
  api group : 'org.jsoup', name: 'jsoup', version: '1.13.1'
}
