package cn.tursom.socket

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool

interface BufferedAsyncSocket : AsyncSocket {
  val pool: MemoryPool
  suspend fun read(timeout: Long = 0L): ByteBuffer = read(pool, timeout)
}