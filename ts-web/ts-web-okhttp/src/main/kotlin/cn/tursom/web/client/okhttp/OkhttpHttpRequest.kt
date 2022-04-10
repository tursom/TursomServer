package cn.tursom.web.client.okhttp

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.web.client.HttpRequest
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OkhttpHttpRequest(
  private val client: OkHttpClient,
  override var method: String,
  url: String,
) : HttpRequest {
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
  override val params = HashMap<String, MutableList<String>>()
  private var ref: String?
  private var body: ByteBuffer? = null

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

  private val paramStr: String
    get() = buildString {
      params.forEach { (k, list) ->
        list.forEach { v ->
          if (isNotEmpty()) {
            append('&')
          }
          append("$k=$v")
        }
      }
    }

  override fun addParam(key: String, value: String) {
    params.getOrPut(key) { ArrayList() }.add(value)
  }

  data class Header(override val key: String, override val value: String) : Map.Entry<String, String>

  override val headers = ArrayList<Header>()

  override fun addHeader(key: String, value: Any): OkhttpHttpRequest {
    headers.add(Header(key, value.toString()))
    return this
  }

  override fun body(data: ByteBuffer) {
    body = data
  }

  override suspend fun send(): OkhttpHttpResponse {
    val builder = Request.Builder()
      .method(method, body?.getBytes()?.toRequestBody())
      .url(buildString {
        append("$protocol://$host$portStr$path")
        val paramStr = paramStr
        if (paramStr.isNotEmpty()) {
          append("?$paramStr")
        }
        if (ref != null) {
          append("#$ref")
        }
      })
    headers.forEach { (key, value) ->
      builder.addHeader(key, value)
    }
    return OkhttpHttpResponse(suspendCoroutine<Response> {
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
