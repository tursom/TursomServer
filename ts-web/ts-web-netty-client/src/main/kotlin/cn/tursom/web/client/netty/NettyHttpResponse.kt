package cn.tursom.web.client.netty

import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.web.client.ChannelHttpResponse
import cn.tursom.web.client.HttpResponse
import cn.tursom.web.client.HttpResponseStream
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

class NettyHttpResponse(
  private val response: io.netty.handler.codec.http.HttpResponse,
  channel: ReceiveChannel<HttpObject>,
) : HttpResponse {
  override val code: Int
    get() = response.status().code()
  override val reason: String
    get() = response.status().reasonPhrase()
  override val headers by lazy { response.headers().map { (k, v) -> k to v } }

  override fun getHeader(key: String): String? = response.headers().get(key)
  override fun getHeaders(key: String): List<String> = response.headers().getAll(key)

  override val body: HttpResponseStream

  init {
    body = NettyStream(response, channel)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private class NettyStream(
    response: HttpObject,
    private val channel: ReceiveChannel<HttpObject>,
  ) : ChannelHttpResponse() {
    override val bufferChannel = GlobalScope.produce {
      if (response is HttpContent) {
        send(NettyByteBuffer(response.content()))
      }
      do {
        val receive = this@NettyStream.channel.receiveCatching()
        if (receive.isSuccess) {
          val content = receive.getOrThrow() as HttpContent
          send(NettyByteBuffer(content.content()))
        } else {
          val e = receive.exceptionOrNull()
          if (e != null) {
            close(e)
          }
        }
      } while (receive.isSuccess)
    }
  }
}