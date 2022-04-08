package cn.tursom.web.client.netty

import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpObject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

interface NettyHttpConnection {
  val channel: SocketChannel

  suspend fun request(request: FullHttpRequest): ReceiveChannel<HttpObject> {
    val ktChannel = Channel<HttpObject>(Channel.UNLIMITED)
    channel.attr(NettyHttpResultResume.recvChannelKey).set(ktChannel)
    channel.writeAndFlush(request)
    return ktChannel
  }
}