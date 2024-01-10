package cn.tursom.http.client;

import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import kotlin.jvm.JvmOverloads;

@JvmOverloads
@OkhttpMaker
inline fun Call.Factory.put(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): Response = newCall {
  put(body())
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.put(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): Response = newCall {
  put(body)
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.put(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): Response = put(url, headers) {
  form {
    add(param)
  }
}

@JvmOverloads
fun Call.Factory.put(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
) = put(url, headers) {
  body.toRequestBody("text/plain;charset=utf-8".toMediaTypeOrNull())
}

@JvmOverloads
fun Call.Factory.put(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
) = put(url, headers) {
  body.asRequestBody("application/octet-stream".toMediaTypeOrNull())
}

@JvmOverloads
fun Call.Factory.put(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
) = put(url, headers) {
  body.toRequestBody("application/octet-stream".toMediaTypeOrNull())
}

@JvmOverloads
fun Call.Factory.putStr(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): String = put(url, headers, body).body!!.string()

@JvmOverloads
fun Call.Factory.putStr(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): String = put(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.putStr(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): String = put(url, param, headers).body!!.string()

@JvmOverloads
fun Call.Factory.putStr(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): String = put(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.putStr(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): String = put(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.putStr(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): String = put(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.putBytes(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): ByteArray = put(url, headers, body).body!!.bytes()

@JvmOverloads
fun Call.Factory.putBytes(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): ByteArray = put(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.putBytes(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): ByteArray = put(url, param, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.putBytes(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): ByteArray = put(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.putBytes(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): ByteArray = put(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.putBytes(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): ByteArray = put(url, body, headers).body!!.bytes()

@JvmOverloads
inline fun <reified T : Any> Call.Factory.putJson(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): T = put(url, headers, body).body!!.json()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJson(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJson(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): T = put(url, param, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJson(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJson(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJson(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.json()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJsonTyped(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): T = put(url, headers, body).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJsonTyped(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJsonTyped(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): T = put(url, param, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJsonTyped(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJsonTyped(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.jsonTyped()

@JvmOverloads
inline fun <reified T : Any>  Call.Factory.putJsonTyped(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
): T = put(url, body, headers).body!!.jsonTyped()
