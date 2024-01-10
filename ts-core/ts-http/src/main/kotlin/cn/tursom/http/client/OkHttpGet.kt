package cn.tursom.http.client

import okhttp3.Call
import okhttp3.Response

@JvmOverloads
fun Call.Factory.get(
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
fun Call.Factory.getStr(
  url: String,
  param: Map<String, String>? = null,
  headers: Map<String, String>? = null,
): String = get(url, param, headers).body!!.string()

@JvmOverloads
fun Call.Factory.getByteArray(
  url: String,
  param: Map<String, String>? = null,
  headers: Map<String, String>? = null,
): ByteArray = get(url, param, headers).body!!.bytes()
