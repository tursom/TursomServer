package cn.tursom.web.netty.ws

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.web.netty.NettyByteBuffer
import cn.tursom.web.ws.TextWebSocketFrame

class NettyTextWebSocketFrame(
  @Suppress("MemberVisibilityCanBePrivate") val frame: io.netty.handler.codec.http.websocketx.TextWebSocketFrame
) : TextWebSocketFrame {
  override val text: String by lazy { data.toString(data.readable) }
  override val data: ByteBuffer = NettyByteBuffer(frame.content())
}