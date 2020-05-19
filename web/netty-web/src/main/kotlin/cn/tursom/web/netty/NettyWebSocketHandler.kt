package cn.tursom.web.netty

import cn.tursom.utils.bytebuffer.NettyByteBuffer
import cn.tursom.web.WebSocketHandler
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class NettyWebSocketHandler(
  channel: Channel,
  private val handler: WebSocketHandler<NettyWebSocketContext>
) : SimpleChannelInboundHandler<WebSocketFrame>() {
  private val webSocketContext = NettyWebSocketContext(channel)

  override fun channelRead0(ctx: ChannelHandlerContext, msg: WebSocketFrame) {
    when (msg) {
      is TextWebSocketFrame -> {
        handler.recvText(NettyByteBuffer(msg.content()), webSocketContext)
      }
      is BinaryWebSocketFrame -> {
        handler.recvBinary(NettyByteBuffer(msg.content()), webSocketContext)
      }
    }
  }
}