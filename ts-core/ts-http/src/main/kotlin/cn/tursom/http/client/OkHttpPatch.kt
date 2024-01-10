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
inline fun Call.Factory.patch(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): Response = newCall {
  put(body())
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.patch(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): Response = newCall {
  put(body)
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.patch(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): Response = patch(url, headers) {
  form {
    add(param)
  }
}

@JvmOverloads
fun Call.Factory.patch(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
) = patch(url, headers) {
  body.toRequestBody()
}

@JvmOverloads
fun Call.Factory.patch(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
) = patch(url, headers) {
  body.toRequestBody()
}

@JvmOverloads
fun Call.Factory.patch(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
) = patch(url, headers) {
  body.asRequestBody()
}

@JvmOverloads
fun Call.Factory.patchStr(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): String = patch(url, headers, body).body!!.string()

@JvmOverloads
fun Call.Factory.patchStr(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): String = patch(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.patchStr(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): String = patch(url, param, headers).body!!.string()

@JvmOverloads
fun Call.Factory.patchStr(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): String = patch(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.patchStr(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): String = patch(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.patchStr(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): String = patch(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.patchBytes(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): ByteArray = patch(url, headers, body).body!!.bytes()

@JvmOverloads
fun Call.Factory.patchBytes(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): ByteArray = patch(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.patchBytes(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): ByteArray = patch(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.patchBytes(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): ByteArray = patch(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.patchBytes(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): ByteArray = patch(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.patchBytes(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): ByteArray = patch(url, param, headers).body!!.bytes()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJson(
  url: String,
  json: String,
  headers: Map<String, String>? = null,
): T = patch(url, json, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJson(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): T = patch(url, body, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJson(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): T = patch(url, param, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJson(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): T = patch(url, body, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJson(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): T = patch(url, body, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJson(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): T = patch(url, headers, body).body!!.json()

// patchJsonTyped
@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJsonTyped(
  url: String,
  json: String,
  headers: Map<String, String>? = null,
): T = patch(url, json, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJsonTyped(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): T = patch(url, body, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJsonTyped(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): T = patch(url, param, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJsonTyped(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): T = patch(url, body, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJsonTyped(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): T = patch(url, body, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.patchJsonTyped(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): T = patch(url, headers, body).body!!.jsonTyped()
