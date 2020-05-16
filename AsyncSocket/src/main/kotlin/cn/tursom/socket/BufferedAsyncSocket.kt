package cn.tursom.socket

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.AsyncSocket

interface BufferedAsyncSocket : AsyncSocket {
  val pool: MemoryPool
  suspend fun read(timeout: Long = 0L): ByteBuffer = read(pool, timeout)
}