package cn.tursom.socket.enhance

import cn.tursom.core.pool.MemoryPool
import java.io.Closeable

interface SocketReader<T> : Closeable {
  suspend fun read(pool: MemoryPool, timeout: Long = 0): T
  override fun close()
}