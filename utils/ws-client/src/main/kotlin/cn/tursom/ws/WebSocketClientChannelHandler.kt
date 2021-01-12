package cn.tursom.ws

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx.*
import io.netty.util.CharsetUtil


class WebSocketClientChannelHandler(
  val client: WebSocketClient,
  val handler: WebSocketHandler
) : SimpleChannelInboundHandler<WebSocketFrame>() {

  override fun channelInactive(ctx: ChannelHandlerContext) {
    handler.onClose(client)
    if (client.ch == ctx.channel()) {
      client.ch = null
    }
  }

  override fun channelRead0(ctx: ChannelHandlerContext, msg: WebSocketFrame) {
    val ch = ctx.channel()
    when (msg) {
      is TextWebSocketFrame -> handler.readMessage(client, msg)
      is BinaryWebSocketFrame -> handler.readMessage(client, msg)
      is CloseWebSocketFrame -> ch.close()
    }
  }
}