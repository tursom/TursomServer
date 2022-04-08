package cn.tursom.web.client.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.web.client.HttpResponse
import cn.tursom.web.client.HttpResponseStream
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpObject
import kotlinx.coroutines.channels.ReceiveChannel

class NettyHttpResponse(
  private val response: io.netty.handler.codec.http.HttpResponse,
  channel: ReceiveChannel<HttpObject>,
) : HttpResponse {
  override val code: Int
    get() = response.status().code()
  override val reason: String
    get() = response.status().reasonPhrase()
  override val headers get() = response.headers()!!

  override fun getHeader(key: String): String? = response.headers().get(key)
  override fun getHeaders(key: String): List<String> = response.headers().getAll(key)

  override val body: HttpResponseStream = NettyStream(response, channel)

  private class NettyStream(
    response: HttpObject,
    private val channel: ReceiveChannel<HttpObject>,
  ) : HttpResponseStream {
    private var buffer: ByteBuffer? = if (response is HttpContent) {
      NettyByteBuffer(response.content())
    } else {
      null
    }

    private suspend fun buffer(): ByteBuffer? {
      if (buffer == null || buffer?.readable == 0) {
        val receive = channel.receiveCatching()
        buffer = if (receive.isSuccess) {
          val content = receive.getOrThrow() as HttpContent
          NettyByteBuffer(content.content())
        } else {
          val e = receive.exceptionOrNull()
          if (e != null) {
            throw e
          }
          null
        }
      }
      return buffer
    }

    override suspend fun skip(n: Long) {
      var skip = 0L
      while (skip < n) {
        val buffer = buffer() ?: return
        skip += buffer.skip((n - skip).toInt())
      }
    }

    override suspend fun read(): Int {
      val buffer = buffer() ?: return -1
      return buffer.get().toInt()
    }

    override suspend fun read(buffer: ByteBuffer) {
      val buf = buffer() ?: return
      buf.writeTo(buffer)
    }

    override fun close() {
      channel.cancel()
    }
  }
}