package cn.tursom.forward.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.unaryPlus
import cn.tursom.forward.Forward
import cn.tursom.forward.NettyHandler
import cn.tursom.utils.bytebuffer.NettyByteBuffer
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.handler.timeout.WriteTimeoutException
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory

class NettyForward(
  private val channel: Channel,
  override var forward: Forward? = null
) : NettyHandler, Forward {
  companion object {
    private val log: InternalLogger = Slf4JLoggerFactory.getInstance(NettyForward::class.java)
  }

  init {
    forward?.forward = this
  }

  override fun write(buffer: ByteBuffer) {
    log.debug("recv msg from agent {}", +{ buffer.toString(buffer.readable) })
    val future = channel.writeAndFlush(Unpooled.wrappedBuffer(buffer.readBuffer()))
    future.addListener {
      buffer.close()
    }
  }

  override fun close() {
    channel.close()
  }

  override fun recvBytes(buf: ByteBuf, ctx: ChannelHandlerContext) {
    val buffer = NettyByteBuffer(buf)
    log.debug(
      "recv msg from socket client {}: {}",
      ctx.channel().remoteAddress(),
      +{ buffer.toString(buffer.readable) })
    forward?.write(buffer) ?: buf.release()
  }

  override fun exceptionCaused(cause: Throwable, ctx: ChannelHandlerContext) {
    if (cause !is ReadTimeoutException && cause !is WriteTimeoutException) {
      log.error("exception caused on socket forward", cause)
    }
    ctx.close()
  }

  override fun onClose(ctx: ChannelHandlerContext) {
    forward?.close()
  }
}