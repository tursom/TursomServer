package cn.tursom.channel.enhance

import cn.tursom.core.pool.MemoryPool
import java.io.Closeable

interface ChannelReader<T> : Closeable {
  suspend fun read(pool: MemoryPool, timeout: Long = 0): T
  override fun close()
}