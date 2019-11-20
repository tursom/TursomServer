package cn.tursom.socket.enhance

import cn.tursom.core.pool.MemoryPool

interface BufferedSocketReader<T> : SocketReader<T> {
  val memoryPool: MemoryPool
  suspend fun read(timeout: Long) = read(memoryPool, timeout)
}