package cn.tursom.web.client

import cn.tursom.core.buffer.ByteBuffer

interface HttpRequest {
  var version: String
  var method: String
  var path: String

  val params: Map<String, List<String>>
  fun addParam(key: String, value: String)
  fun addParams(params: Map<String, String>) {
    params.forEach(::addParam)
  }

  val headers: Iterable<Map.Entry<String, String>>
  fun addHeader(key: String, value: Any): HttpRequest
  fun addHeaders(headers: Map<String, Any>): HttpRequest {
    headers.forEach(::addHeader)
    return this
  }

  fun body(data: ByteBuffer)

  suspend fun send(): HttpResponse
}
