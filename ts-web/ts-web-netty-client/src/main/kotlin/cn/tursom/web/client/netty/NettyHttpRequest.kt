package cn.tursom.web.client.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.core.toStartWith
import cn.tursom.log.impl.Slf4jImpl
import cn.tursom.web.client.HttpRequest
import cn.tursom.web.client.HttpResponse
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpVersion

class NettyHttpRequest(
  private val pool: HttpConnectionPool,
  val scheme: String,
  val host: String,
  val port: Int,
  path: String,
) : HttpRequest {
  companion object : Slf4jImpl()

  private val portStr: String
    get() = if (port <= 0) {
      ""
    } else {
      ":$port"
    }

  private var request: FullHttpRequest = DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "")

  override var version: String
    get() = request.protocolVersion().text()
    set(value) {
      request.protocolVersion = HttpVersion.valueOf(value)
    }

  override var method: String
    get() = request.method().name()
    set(value) {
      request.method = HttpMethod.valueOf(value)
    }

  override var path: String = path.toStartWith('/')
    set(value) {
      field = value.toStartWith('/')
    }

  override val params = HashMap<String, ArrayList<String>>().withDefault { ArrayList() }

  override fun addParam(key: String, value: String) {
    params[key]!!.add(value)
  }

  override val headers: Iterable<Map.Entry<String, String>>
    get() = request.headers()

  override fun addHeader(key: String, value: Any): NettyHttpRequest {
    request.headers().add(key, value)
    return this
  }

  override fun body(data: ByteBuffer) {
    val byteBuf = if (data is NettyByteBuffer) {
      data.byteBuf
    } else {
      Unpooled.wrappedBuffer(data.getBytes())
    }
    request = request.replace(byteBuf)
  }

  override suspend fun send(): HttpResponse {
    request.uri = buildString {
      append("$scheme://$host$portStr$path")
      append(buildString {
        params.forEach { (key, params) ->
          if (isEmpty()) {
            append("?")
          }
          params.forEach { param ->
            if (isNotEmpty()) {
              append('&')
            }
            append("$key=$param")
          }
        }
      })
    }
    val receiveChannel = pool.useConnection {
      it.request(request)
    }
    val httpResponse = receiveChannel.receive() as io.netty.handler.codec.http.HttpResponse
    return NettyHttpResponse(httpResponse, receiveChannel)
  }
}
