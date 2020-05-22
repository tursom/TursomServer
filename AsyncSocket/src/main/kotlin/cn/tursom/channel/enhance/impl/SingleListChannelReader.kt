package cn.tursom.channel.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.channel.enhance.ChannelReader

class SingleListChannelReader(
  private val prevReader: ChannelReader<ByteBuffer>
) : ChannelReader<List<ByteBuffer>> {
  override suspend fun read(pool: MemoryPool, timeout: Long): List<ByteBuffer> = listOf(prevReader.read(pool, timeout))
  override fun close() = prevReader.close()
}