package cn.tursom.channel

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool

interface BufferedAsyncChannel : AsyncChannel {
  val pool: MemoryPool
  val prevChannel: AsyncChannel
  suspend fun read(timeout: Long = 0L): ByteBuffer = read(pool, timeout)
}