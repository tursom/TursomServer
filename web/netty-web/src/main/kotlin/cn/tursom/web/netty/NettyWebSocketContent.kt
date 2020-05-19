package cn.tursom.web.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.utils.bytebuffer.NettyByteBuffer
import cn.tursom.web.WebSocketContent
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

class NettyWebSocketContent(
  private val channel: Channel
) : WebSocketContent {
  override fun writeText(buffer: ByteBuffer) {
    if (buffer is NettyByteBuffer) {
      channel.writeAndFlush(TextWebSocketFrame(buffer.byteBuf))
    } else {
      buffer.read {
        channel.writeAndFlush(TextWebSocketFrame(Unpooled.wrappedBuffer(it)))
      }
      buffer.close()
    }
  }

  override fun writeBinary(buffer: ByteBuffer) {
    if (buffer is NettyByteBuffer) {
      channel.writeAndFlush(BinaryWebSocketFrame(buffer.byteBuf))
    } else {
      buffer.read {
        channel.writeAndFlush(BinaryWebSocketFrame(Unpooled.wrappedBuffer(it)))
      }
      buffer.close()
    }
  }
}