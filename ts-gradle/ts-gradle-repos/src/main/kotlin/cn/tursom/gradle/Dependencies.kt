@file:Suppress("unused", "ObjectPropertyName", "FunctionName", "HasPlatformType", "HasPlatformType")

package cn.tursom.gradle

import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler

private const val defaultVersion = "1.1-SNAPSHOT"

private fun DependencyHandler.add(
  configurationName: String,
  name: String,
  group: String = "cn.tursom",
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = create(mapOf(
  "group" to group,
  "name" to name,
  "version" to version,
  "configuration" to configuration,
  "classifier" to classifier,
  "ext" to ext
).filter { it.value != null }).let {
  it as ExternalModuleDependency
  dependencyConfiguration?.execute(it)
  add(configurationName, it)
  it
}

fun DependencyHandler.tursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("implementation", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.implementationTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("implementation", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.testImplementationTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("testImplementation", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.apiTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("api", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.testApiTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("testApi", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.compileOnlyTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("compileOnly", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.testCompileOnlyTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("testCompileOnly", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.runtimeOnlyTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("runtimeOnly", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

fun DependencyHandler.testRuntimeOnlyTursomServer(
  name: String,
  version: String = defaultVersion,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null,
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = add("testRuntimeOnly", name, "cn.tursom", version, configuration, classifier, ext, dependencyConfiguration)

val DependencyHandler.`ts-core` get() = tursomServer("ts-core")
val DependencyHandler.`ts-async-http` get() = tursomServer("ts-async-http")
val DependencyHandler.`ts-buffer` get() = tursomServer("ts-buffer")
val DependencyHandler.`ts-clone` get() = tursomServer("ts-clone")
val DependencyHandler.`ts-coroutine` get() = tursomServer("ts-coroutine")
val DependencyHandler.`ts-coroutine-lock` get() = tursomServer("ts-coroutine-lock")
val DependencyHandler.`ts-datastruct` get() = tursomServer("ts-datastruct")
val DependencyHandler.`ts-delegation` get() = tursomServer("ts-delegation")
val DependencyHandler.`ts-observer` get() = tursomServer("ts-observer")
val DependencyHandler.`ts-encrypt` get() = tursomServer("ts-encrypt")
val DependencyHandler.`ts-hash` get() = tursomServer("ts-hash")
val DependencyHandler.`ts-http` get() = tursomServer("ts-http")
val DependencyHandler.`ts-json` get() = tursomServer("ts-json")
val DependencyHandler.`ts-log` get() = tursomServer("ts-log")
val DependencyHandler.`ts-mail` get() = tursomServer("ts-mail")
val DependencyHandler.`ts-pool` get() = tursomServer("ts-pool")
val DependencyHandler.`ts-ws-client` get() = tursomServer("ts-ws-client")
val DependencyHandler.`ts-xml` get() = tursomServer("ts-xml")
val DependencyHandler.`ts-yaml` get() = tursomServer("ts-yaml")

val DependencyHandler.`ts-database` get() = tursomServer("ts-database")
val DependencyHandler.`ts-ktorm` get() = tursomServer("ts-ktorm")
val DependencyHandler.`ts-mongodb` get() = tursomServer("ts-mongodb")
val DependencyHandler.`ts-mongodb-spring` get() = tursomServer("ts-mongodb-spring")
val DependencyHandler.`ts-redis` get() = tursomServer("ts-redis")

val DependencyHandler.`ts-gradle` get() = tursomServer("ts-gradle")

val DependencyHandler.`ts-socket` get() = tursomServer("ts-socket")

val DependencyHandler.`ts-web` get() = tursomServer("ts-web")
val DependencyHandler.`ts-web-coroutine` get() = tursomServer("ts-web-coroutine")
val DependencyHandler.`ts-web-netty` get() = tursomServer("ts-web-netty")
val DependencyHandler.`ts-web-netty-client` get() = tursomServer("ts-web-netty-client")
val DependencyHandler.`ts-web-okhttp` get() = tursomServer("ts-web-okhttp")


fun DependencyHandler.`ts-core`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-core", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-async-http`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-async-http", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-buffer`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-buffer", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-clone`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-clone", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-coroutine`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-coroutine", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-coroutine-lock`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-coroutine-lock", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-datastruct`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-datastruct", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-delegation`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-delegation", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-observer`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-observer", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-encrypt`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-encrypt", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-hash`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-hash", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-http`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-http", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-json`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-json", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-log`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-log", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-mail`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-mail", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-pool`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-pool", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-ws-client`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-ws-client", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-xml`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-xml", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-yaml`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-yaml", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.`ts-database`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-database", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-ktorm`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-ktorm", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-mongodb`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-mongodb", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-mongodb-spring`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-mongodb-spring", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-redis`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-redis", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.`ts-gradle`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-gradle", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.`ts-socket`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-socket", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.`ts-web`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-web-coroutine`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-coroutine", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-web-netty`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-netty", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-web-netty-client`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-netty-client", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts-web-okhttp`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-okhttp", dependencyConfiguration = dependencyConfiguration)


val DependencyHandler.ts_core get() = tursomServer("ts-core")
val DependencyHandler.ts_async_http get() = tursomServer("ts-async-http")
val DependencyHandler.ts_buffer get() = tursomServer("ts-buffer")
val DependencyHandler.ts_clone get() = tursomServer("ts-clone")
val DependencyHandler.ts_coroutine get() = tursomServer("ts-coroutine")
val DependencyHandler.ts_coroutine_lock get() = tursomServer("ts-coroutine-lock")
val DependencyHandler.ts_datastruct get() = tursomServer("ts-datastruct")
val DependencyHandler.ts_delegation get() = tursomServer("ts-delegation")
val DependencyHandler.ts_observer get() = tursomServer("ts-observer")
val DependencyHandler.ts_encrypt get() = tursomServer("ts-encrypt")
val DependencyHandler.ts_hash get() = tursomServer("ts-hash")
val DependencyHandler.ts_http get() = tursomServer("ts-http")
val DependencyHandler.ts_json get() = tursomServer("ts-json")
val DependencyHandler.ts_log get() = tursomServer("ts-log")
val DependencyHandler.ts_mail get() = tursomServer("ts-mail")
val DependencyHandler.ts_pool get() = tursomServer("ts-pool")
val DependencyHandler.ts_ws_client get() = tursomServer("ts-ws-client")
val DependencyHandler.ts_xml get() = tursomServer("ts-xml")
val DependencyHandler.ts_yaml get() = tursomServer("ts-yaml")

val DependencyHandler.ts_database get() = tursomServer("ts-database")
val DependencyHandler.ts_ktorm get() = tursomServer("ts-ktorm")
val DependencyHandler.ts_mongodb get() = tursomServer("ts-mongodb")
val DependencyHandler.ts_mongodb_spring get() = tursomServer("ts-mongodb-spring")
val DependencyHandler.ts_redis get() = tursomServer("ts-redis")

val DependencyHandler.ts_gradle get() = tursomServer("ts-gradle")

val DependencyHandler.ts_socket get() = tursomServer("ts-socket")

val DependencyHandler.ts_web get() = tursomServer("ts-web")
val DependencyHandler.ts_web_coroutine get() = tursomServer("ts-web-coroutine")
val DependencyHandler.ts_web_netty get() = tursomServer("ts-web-netty")
val DependencyHandler.ts_web_netty_client get() = tursomServer("ts-web-netty-client")
val DependencyHandler.ts_web_okhttp get() = tursomServer("ts-web-okhttp")


fun DependencyHandler.ts_core(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-core", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_async_http(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-async-http", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_buffer(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-buffer", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_clone(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-clone", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_coroutine(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-coroutine", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.`ts_coroutine-lock`(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-coroutine-lock", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_datastruct(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-datastruct", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_delegation(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-delegation", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_observer(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-observer", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_encrypt(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-encrypt", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_hash(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-hash", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_http(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-http", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_json(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-json", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_log(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-log", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_mail(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-mail", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_pool(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-pool", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_ws_client(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-ws-client", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_xml(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-xml", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_yaml(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-yaml", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.ts_database(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-database", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_ktorm(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-ktorm", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_mongodb(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-mongodb", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_mongodb_spring(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-mongodb-spring", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_redis(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-redis", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.ts_gradle(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-gradle", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.ts_socket(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-socket", dependencyConfiguration = dependencyConfiguration)


fun DependencyHandler.ts_web(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_web_coroutine(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-coroutine", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_web_netty(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-netty", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_web_netty_client(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-netty-client", dependencyConfiguration = dependencyConfiguration)

fun DependencyHandler.ts_web_okhttp(
  dependencyConfiguration: Action<ExternalModuleDependency>? = null
) = tursomServer("ts-web-okhttp", dependencyConfiguration = dependencyConfiguration)
