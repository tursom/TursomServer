package cn.tursom.web.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.log.traceEnabled
import cn.tursom.web.WebSocketContent
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import org.slf4j.LoggerFactory
import java.net.SocketAddress

class NettyWebSocketContent(
  val channel: Channel,
) : WebSocketContent {
  override val remoteAddress: SocketAddress get() = channel.remoteAddress()
  override fun writeText(buffer: ByteBuffer) {
    if (log.traceEnabled) {
      log?.trace("remoteAddress buffer: {}", buffer.toString(buffer.readable))
    }
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
    if (log.traceEnabled) {
      log?.trace("writeBinary buffer: {}", buffer.toString(buffer.readable))
    }
    if (buffer is NettyByteBuffer) {
      channel.writeAndFlush(BinaryWebSocketFrame(buffer.byteBuf))
    } else {
      buffer.read {
        channel.writeAndFlush(BinaryWebSocketFrame(Unpooled.wrappedBuffer(it)))
      }
      buffer.close()
    }
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyWebSocketContent::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}