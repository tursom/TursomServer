package cn.tursom.forward.ws

import cn.tursom.forward.ws.WebSocketHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class WebSocketFrameHandler(private val handler: WebSocketHandler) : SimpleChannelInboundHandler<WebSocketFrame>() {
  override fun channelRead0(ctx: ChannelHandlerContext, msg: WebSocketFrame) {
    if (msg is TextWebSocketFrame) {
      handler.recvStr(msg.content(), ctx)
    } else if (msg is BinaryWebSocketFrame) {
      handler.recvBytes(msg.content(), ctx)
    }
  }

  override fun channelInactive(ctx: ChannelHandlerContext) {
    handler.onClose(ctx)
    super.channelInactive(ctx)
  }

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    handler.exceptionCaused(cause, ctx)
  }

  override fun channelActive(ctx: ChannelHandlerContext) {
    handler.onOpen(ctx)
    super.channelActive(ctx)
  }
}