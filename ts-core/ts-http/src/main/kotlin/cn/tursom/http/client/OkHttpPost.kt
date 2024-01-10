package cn.tursom.http.client

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@JvmOverloads
@OkhttpMaker
inline fun Call.Factory.post(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): Response = newCall {
  post(body())
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.post(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): Response = newCall {
  post(body)
  url(url)
  addHeaders(headers)
}.execute()

@JvmOverloads
fun Call.Factory.post(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): Response = post(url, headers) {
  form {
    add(param)
  }
}

@JvmOverloads
fun Call.Factory.post(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
) = post(url, headers) {
  body.toRequestBody("text/plain;charset=utf-8".toMediaTypeOrNull())
}

@JvmOverloads
fun Call.Factory.post(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
) = post(url, headers) {
  body.asRequestBody("application/octet-stream".toMediaTypeOrNull())
}

@JvmOverloads
fun Call.Factory.post(
  url: String,
  body: ByteArray,
  headers: Map<String, String>? = null,
) = post(url, headers) {
  body.toRequestBody("application/octet-stream".toMediaTypeOrNull())
}

@JvmOverloads
@OkhttpMaker
inline fun Call.Factory.postStr(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): String = post(url, headers, body).body!!.string()

@JvmOverloads
fun Call.Factory.postStr(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): String = post(url, body, headers).body!!.string()

@JvmOverloads
fun Call.Factory.postStr(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): String = postStr(url, headers) {
  FormBody.Builder().add(param).build()
}

@JvmOverloads
fun Call.Factory.postStr(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): String = postStr(url, headers) {
  body.toRequestBody("text/plain;charset=utf-8".toMediaTypeOrNull())
}

@JvmOverloads
fun Call.Factory.postStr(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): String = postStr(url, headers) {
  body.asRequestBody("application/octet-stream".toMediaTypeOrNull())
}

@JvmOverloads
@OkhttpMaker
inline fun Call.Factory.postByteArray(
  url: String,
  headers: Map<String, String>? = null,
  body: Request.Builder.() -> RequestBody,
): ByteArray = post(url, headers, body).body!!.bytes()

@JvmOverloads
fun Call.Factory.postByteArray(
  url: String,
  body: RequestBody,
  headers: Map<String, String>? = null,
): ByteArray = post(url, body, headers).body!!.bytes()

@JvmOverloads
fun Call.Factory.postByteArray(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String>? = null,
): ByteArray = postByteArray(url, headers) {
  FormBody.Builder().add(param).build()
}

@JvmOverloads
fun Call.Factory.postByteArray(
  url: String,
  body: String,
  headers: Map<String, String>? = null,
): ByteArray = postByteArray(url, headers) {
  body.toRequestBody("text/plain;charset=utf-8".toMediaTypeOrNull())
}

@JvmOverloads
fun Call.Factory.postByteArray(
  url: String,
  body: File,
  headers: Map<String, String>? = null,
): ByteArray = postByteArray(url, headers) {
  body.asRequestBody("application/octet-stream".toMediaTypeOrNull())
}
