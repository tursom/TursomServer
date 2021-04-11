package cn.tursom.channel.enhance

import cn.tursom.core.pool.MemoryPool

interface BufferedChannelReader<T> : ChannelReader<T> {
  val memoryPool: MemoryPool
  suspend fun read(timeout: Long) = read(memoryPool, timeout)
}