package cn.tursom.web.netty

import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.log.traceEnabled
import cn.tursom.web.utils.Chunked
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.stream.ChunkedInput
import org.slf4j.LoggerFactory

class NettyChunkedInput(private val chunked: Chunked) : ChunkedInput<ByteBuf> {
  override fun progress(): Long = chunked.progress
  override fun length() = chunked.length
  override fun isEndOfInput(): Boolean = chunked.endOfInput

  @Deprecated("Deprecated in Java", ReplaceWith("readChunk(allocator)"))
  override fun readChunk(ctx: ChannelHandlerContext?): ByteBuf = readChunk()
  override fun readChunk(allocator: ByteBufAllocator?): ByteBuf = readChunk()

  @Suppress("MemberVisibilityCanBePrivate")
  fun readChunk(): ByteBuf {
    val buf = chunked.readChunk()
    if (log.traceEnabled) {
      log?.trace("readChunk {}", buf)
    }
    return if (buf is NettyByteBuffer) buf.byteBuf
    else Unpooled.wrappedBuffer(buf.readBuffer())
  }

  override fun close() {
    if (log.traceEnabled) {
      log?.trace("close")
    }
    chunked.close()
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyChunkedInput::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}