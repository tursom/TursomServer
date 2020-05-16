package cn.tursom.channel

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.AsyncSocket

interface BufferedAsyncChannel : AsyncChannel {
  val pool: MemoryPool
  suspend fun read(timeout: Long = 0L): ByteBuffer = read(pool, timeout)
}