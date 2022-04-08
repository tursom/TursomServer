package cn.tursom.web.client.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
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
) : HttpRequest {
  companion object : Slf4jImpl()

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
  override var path: String
    get() = request.uri()
    set(value) {
      request.uri = value
    }
  override val params = HashMap<String, ArrayList<String>>().withDefault { ArrayList() }

  override fun addParam(key: String, value: String) {
    params[key]!!.add(value)
  }

  override val headers: Iterable<Map.Entry<String, String>>
    get() = request.headers()

  override fun addHeader(key: String, value: Any) {
    request.headers().add(key, value)
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
    val receiveChannel = pool.useConnection {
      it.request(request)
    }
    val httpResponse = receiveChannel.receive() as io.netty.handler.codec.http.HttpResponse
    return NettyHttpResponse(httpResponse, receiveChannel)
  }
}
