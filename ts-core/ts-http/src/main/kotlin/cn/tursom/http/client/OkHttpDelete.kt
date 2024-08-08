package cn.tursom.http.client

import okhttp3.Call
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File

@JvmOverloads
@OkhttpMaker
inline fun Call.Factory.delete(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): Response = newCall {
  delete(body())
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.delete(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): Response = newCall {
  delete(body)
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
@JvmName("deleteWithParam")
fun Call.Factory.delete(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): Response = delete(url, headers) {
  form {
    add(param)
  }
}

@JvmOverloads
fun Call.Factory.delete(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
) = delete(url, headers) {
  body.toRequestBody()
}

@JvmOverloads
fun Call.Factory.delete(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
) = delete(url, headers) {
  body.toRequestBody()
}

@JvmOverloads
fun Call.Factory.delete(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
) = delete(url, headers) {
  body.asRequestBody()
}

@JvmOverloads
fun Call.Factory.delete(
  url: String,
  param: Map<String, String?>? = null,
  headers: Map<String, String>? = null,
): Response = newCall {
  url(url) {
    addQueryParameters(param)
  }
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.deleteStr(
  url: String,
  param: Map<String, String>? = null,
  headers: Map<String, String>? = null,
): String = delete(url, param, headers).body!!.string()

@JvmOverloads
fun Call.Factory.deleteByteArray(
  url: String,
  param: Map<String, String>? = null,
  headers: Map<String, String>? = null,
): ByteArray = delete(url, param, headers).body!!.bytes()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.deleteJson(
  url: String,
  param: Map<String, String>? = null,
  headers: Map<String, String>? = null,
): String = delete(url, param, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.deleteJsonTyped(
  url: String,
  param: Map<String, String>? = null,
  headers: Map<String, String>? = null,
): T = delete(url, param, headers).body!!.jsonTyped()


