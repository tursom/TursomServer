package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool

class StringReader(
  private val prevReader: ChannelReader<ByteBuffer>
) : ChannelReader<String> {
  override suspend fun read(pool: MemoryPool, timeout: Long) = prevReader.read(pool, timeout).let { buffer ->
    val string = buffer.getString()
    buffer.close()
    string
  }

  override fun close() = prevReader.close()
}