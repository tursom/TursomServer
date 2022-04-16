package cn.tursom.web.client.okhttp

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.web.client.HttpRequest
import cn.tursom.web.client.ParamType
import cn.tursom.web.client.ParamsHolder
import kotlinx.coroutines.channels.ReceiveChannel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OkhttpHttpRequest(
  private val client: OkHttpClient,
  override var method: String,
  url: String,
) : HttpRequest<OkhttpHttpRequest>, ParamsHolder<OkhttpHttpRequest> {
  override var version: String = ""
  private val protocol: String
  private val host: String
  private val port: Int
  override var path: String = ""
    set(value) {
      field = if (value.startsWith('/')) {
        value
      } else {
        "/$value"
      }
    }
  override var paramType: ParamType = if (method in setOf("POST", "PUT", "PATCH")) ParamType.WEB_FORM else ParamType.URL
  override val params = HashMap<String, MutableList<String>>()
  private var ref: String?
  private var body: RequestBody? = null

  init {
    val url = URL(url)
    host = url.host
    port = url.port
    path = url.path
    protocol = url.protocol
    url.query?.splitToSequence('&')?.forEach { query ->
      val i = query.indexOf('=')
      if (i <= 0) {
        return@forEach
      }
      val key = query.substring(0, i)
      val value = query.substring(i + 1)
      addParam(key, value)
    }
    ref = url.ref
  }

  private val portStr: String
    get() = if (port <= 0) {
      ""
    } else {
      ":$port"
    }

  data class Header(override val key: String, override val value: String) : Map.Entry<String, String>

  override val headers = ArrayList<Header>()

  override fun addHeader(key: String, value: Any): OkhttpHttpRequest {
    headers.add(Header(key, value.toString()))
    return this
  }

  override fun body(channel: ReceiveChannel<ByteBuffer>) = throw UnsupportedOperationException()
  override fun body(data: ByteBuffer) = body(data.getBytes())
  override fun body(bytes: ByteArray) = body(bytes.toRequestBody())
  override fun body(string: String) = body(string.toRequestBody("text/plain;charset=utf-8".toMediaTypeOrNull()))
  override fun body(file: File) = body(file.asRequestBody("application/octet-stream".toMediaTypeOrNull()))

  fun body(body: RequestBody): OkhttpHttpRequest {
    this.body = body
    return this
  }

  override suspend fun send(): OkhttpHttpResponse {
    val paramStr = paramStr
    var body = body
    if (body == null && paramType == ParamType.WEB_FORM) {
      body = paramStr.toByteArray().toRequestBody()
    }
    val builder = Request.Builder()
      .method(method, body)
      .url(buildString {
        append("$protocol://$host$portStr$path")
        if (paramType == ParamType.URL && paramStr.isNotEmpty()) {
          append("?$paramStr")
        }
        if (ref != null) {
          append("#$ref")
        }
      })
    headers.forEach { (key, value) ->
      builder.addHeader(key, value)
    }
    return OkhttpHttpResponse(suspendCoroutine {
      client.newCall(builder.build()).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          it.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
          it.resume(response)
        }
      })
    })
  }
}
