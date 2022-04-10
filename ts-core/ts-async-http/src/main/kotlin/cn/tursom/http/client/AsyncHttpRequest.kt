package cn.tursom.http.client

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File

@Deprecated("this object is deprecated. it will be removed on 2022/5")
@Suppress("unused", "MemberVisibilityCanBePrivate")
object AsyncHttpRequest {
  @Deprecated("it should replace by Okhttp.defaultClient",
    ReplaceWith("Okhttp.default"))
  var defaultClient: OkHttpClient by Okhttp::default

  @Deprecated("it should replace by Okhttp.socket",
    ReplaceWith("Okhttp.socket"))
  val socketClient: OkHttpClient by Okhttp::socket

  @Deprecated("it should replace by Okhttp.httpProxy",
    ReplaceWith("Okhttp.httpProxy"))
  val httpProxyClient: OkHttpClient by Okhttp::httpProxy

  @Deprecated("it should replace by call.sendRequest()",
    ReplaceWith("call.sendRequest()"))
  suspend fun sendRequest(call: Call): Response = call.sendRequest()

  @JvmOverloads
  @Deprecated("it should replace by client.get()",
    ReplaceWith("client.get(url, param, headers)"))
  suspend fun get(
    url: String,
    param: Map<String, String>? = null,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): Response = client.get(url, param, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.post()",
    ReplaceWith("client.post(url, body, headers)"))
  suspend fun post(
    url: String,
    body: RequestBody,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): Response = client.post(url, body, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.post()",
    ReplaceWith("client.post(url, param, headers)"))
  suspend fun post(
    url: String,
    param: Map<String, String>,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): Response = client.post(url, param, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.post()",
    ReplaceWith("client.post(url, body, headers)"))
  suspend fun post(
    url: String,
    body: String,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ) = client.post(url, body, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.post()",
    ReplaceWith("client.post(url, body, headers)"))
  suspend fun post(
    url: String,
    body: File,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ) = client.post(url, body, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.post()",
    ReplaceWith("client.post(url, body, headers)"))
  suspend fun post(
    url: String,
    body: ByteArray,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ) = client.post(url, body, headers)

  @Suppress("BlockingMethodInNonBlockingContext")
  @JvmOverloads
  @Deprecated("it should replace by client.getStr()",
    ReplaceWith("client.getStr(url, param, headers)"))
  suspend fun getStr(
    url: String,
    param: Map<String, String>? = null,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): String = client.getStr(url, param, headers)

  @Suppress("BlockingMethodInNonBlockingContext")
  @JvmOverloads
  @Deprecated("it should replace by client.postStr()",
    ReplaceWith("client.postStr(url, body, headers)"))
  suspend fun postStr(
    url: String,
    body: RequestBody,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): String = client.postStr(url, body, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.postStr()",
    ReplaceWith("client.postStr(url, param, headers)"))
  suspend fun postStr(
    url: String,
    param: Map<String, String>,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): String = client.postStr(url, param, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.postStr()",
    ReplaceWith("client.postStr(url, body, headers)"))
  suspend fun postStr(
    url: String,
    body: String,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): String = client.postStr(url, body, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.postStr()",
    ReplaceWith("client.postStr(url, body, headers)"))
  suspend fun postStr(
    url: String,
    body: File,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): String = client.postStr(url, body, headers)

  @Suppress("BlockingMethodInNonBlockingContext")
  @JvmOverloads
  @Deprecated("it should replace by client.getByteArray()",
    ReplaceWith("client.getByteArray(url, param, headers)"))
  suspend fun getByteArray(
    url: String,
    param: Map<String, String>? = null,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): ByteArray = client.getByteArray(url, param, headers)


  @Suppress("BlockingMethodInNonBlockingContext")
  @JvmOverloads
  @Deprecated("it should replace by client.postByteArray()",
    ReplaceWith("client.postByteArray(url, body, headers)"))
  suspend fun postByteArray(
    url: String,
    body: RequestBody,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): ByteArray = client.postByteArray(url, body, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.postByteArray()",
    ReplaceWith("client.postByteArray(url, param, headers)"))
  suspend fun postByteArray(
    url: String,
    param: Map<String, String>,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): ByteArray = client.postByteArray(url, param, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.postByteArray()",
    ReplaceWith("client.postByteArray(url, body, headers)"))
  suspend fun postByteArray(
    url: String,
    body: String,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): ByteArray = client.postByteArray(url, body, headers)

  @JvmOverloads
  @Deprecated("it should replace by client.postByteArray()",
    ReplaceWith("client.postByteArray(url, body, headers)"))
  suspend fun postByteArray(
    url: String,
    body: File,
    headers: Map<String, String>? = null,
    client: OkHttpClient = Okhttp.default,
  ): ByteArray = client.postByteArray(url, body, headers)
}