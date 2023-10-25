package cn.tursom.web.client

import cn.tursom.core.ByteBufferUtil
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.util.uncheckedCast
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.File

interface HttpRequest<out T : HttpRequest<T>> : ParamsSetter<T> {
  var version: String
  var method: String
  var path: String

  var paramType: ParamType
  val params: Map<String, List<String>>
  override fun addParams(params: Map<String, String>): T {
    params.forEach(::addParam)
    return uncheckedCast()
  }

  val headers: Iterable<Map.Entry<String, String>>
  fun addHeader(key: String, value: Any): T
  fun addHeaders(headers: Map<String, Any>): T {
    headers.forEach(::addHeader)
    return uncheckedCast()
  }

  fun body(channel: ReceiveChannel<ByteBuffer>): T
  fun body(data: ByteBuffer): T
  fun body(bytes: ByteArray): T = body(ByteBufferUtil.wrap(bytes, false))
  fun body(string: String): T = body(string.toByteArray())
  fun body(file: File): T {
    body(ByteBufferUtil.wrap(file.readBytes(), false))
    return uncheckedCast()
  }

  suspend fun send(): HttpResponse
}
