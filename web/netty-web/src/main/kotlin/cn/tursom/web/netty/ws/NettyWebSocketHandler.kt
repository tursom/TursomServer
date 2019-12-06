package cn.tursom.web.netty.ws

import cn.tursom.web.netty.NettyExceptionContent
import cn.tursom.web.ws.WebSocketHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class NettyWebSocketHandler(
  val handler: WebSocketHandler<NettyExceptionContent>
) : SimpleChannelInboundHandler<WebSocketFrame>() {
  override fun channelRead0(ctx: ChannelHandlerContext?, msg: WebSocketFrame?) {

  }
}