package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer

class ByteArrayWriter(
  private val prevWriter: ChannelWriter<ByteBuffer>,
) : ChannelWriter<ByteArray> {
  override suspend fun write(value: ByteArray) {
    prevWriter.write(HeapByteBuffer(value))
  }

  override suspend fun flush(timeout: Long): Long = prevWriter.flush(timeout)
  override suspend fun writeAndFlush(value: ByteArray, timeout: Long): Long =
    prevWriter.writeAndFlush(HeapByteBuffer(value), timeout)

  override fun close() = prevWriter.close()
}