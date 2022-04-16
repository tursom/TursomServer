package cn.tursom.channel.enhance.impl

import cn.tursom.channel.AsyncChannel
import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool

class ChannelReaderImpl(
  private val socket: AsyncChannel,
) : ChannelReader<ByteBuffer> {
  override suspend fun read(pool: MemoryPool, timeout: Long) = socket.read(pool, timeout)
  override fun close() = socket.close()
}