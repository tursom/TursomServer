package cn.tursom.core.ws

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.*


class WebSocketClientChannelHandler<T : WebSocketClient<T, H>, H : WebSocketHandler<T, H>>(
  val client: T,
  val handler: WebSocketHandler<T, H>,
  private val autoRelease: Boolean = true,
) : SimpleChannelInboundHandler<WebSocketFrame>(autoRelease) {

  override fun channelInactive(ctx: ChannelHandlerContext) {
    client.onClose()
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
      is PingWebSocketFrame -> handler.readPing(client, msg)
      is PongWebSocketFrame -> handler.readPong(client, msg)
      is CloseWebSocketFrame -> {
        if (!autoRelease) while (msg.refCnt() != 0) msg.release()
        ch.close()
      }
      else -> if (!autoRelease) while (msg.refCnt() != 0) msg.release()
    }
  }
}