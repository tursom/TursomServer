package cn.tursom.web.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.log.traceEnabled
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.stream.ChunkedInput
import org.slf4j.LoggerFactory

class NettyChunkedByteBuffer(
  private val bufList: Iterable<() -> ByteBuffer>
) : ChunkedInput<ByteBuf> {
  constructor(vararg bufList: ByteBuffer) : this(bufList.map { { it } } as Iterable<() -> ByteBuffer>)
  constructor(vararg bufList: () -> ByteBuffer) : this(bufList.asList())
  constructor(bufList: List<ByteBuffer>) : this(bufList.map { { it } } as Iterable<() -> ByteBuffer>)

  private var next: ByteBuffer? = null
  val iterator = bufList.iterator()
  private var progress: Long = 0

  override fun progress(): Long = progress
  override fun length(): Long = -1
  override fun isEndOfInput(): Boolean = !iterator.hasNext()

  override fun readChunk(ctx: ChannelHandlerContext?): ByteBuf = readChunk()
  override fun readChunk(allocator: ByteBufAllocator?): ByteBuf = readChunk()

  private fun readChunk(): ByteBuf {
    if (log.traceEnabled) {
      log?.trace("readChunk")
    }
    this.next?.close()
    val next = iterator.next()()
    this.next = next
    progress += next.readable
    return if (next is NettyByteBuffer) next.byteBuf
    else Unpooled.wrappedBuffer(next.readBuffer())
  }

  override fun close() {
    if (log.traceEnabled) {
      log?.trace("close")
    }
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyChunkedByteBuffer::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}

