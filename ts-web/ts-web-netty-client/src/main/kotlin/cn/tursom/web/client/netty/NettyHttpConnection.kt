package cn.tursom.web.client.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.core.coroutine.GlobalScope
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.DefaultHttpContent
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface NettyHttpConnection {
  companion object {
    suspend fun request(
      pool: HttpConnectionPool,
      request: HttpRequest,
      bodyChannel: ReceiveChannel<ByteBuffer>? = null,
    ): ReceiveChannel<HttpObject> {
      val ktChannel = Channel<HttpObject>(Channel.UNLIMITED)
      if (bodyChannel != null) GlobalScope.launch {
        pool.useConnection { conn ->
          conn.request(request, bodyChannel, ktChannel)
        }
      } else pool.useConnection { conn ->
        conn.request(request, bodyChannel, ktChannel)
      }
      return ktChannel
    }
  }

  val channel: SocketChannel

  suspend fun request(
    request: HttpRequest,
    bodyChannel: ReceiveChannel<ByteBuffer>? = null,
    ktChannel: Channel<HttpObject> = Channel(Channel.UNLIMITED),
  ): ReceiveChannel<HttpObject> {
    channel.attr(NettyHttpResultResume.recvChannelKey).set(ktChannel)
    channel.writeAndFlush(request)
    bodyChannel?.receiveAsFlow()?.collect {
      send(it)
    }
    return ktChannel
  }

  fun send(byteBuffer: ByteBuffer) {
    channel.writeAndFlush(DefaultHttpContent(NettyByteBuffer.toByteBuf(byteBuffer)))
  }
}