package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.AsyncSocket
import cn.tursom.socket.enhance.SocketReader

class SocketReaderImpl(
  private val socket: AsyncSocket
) : SocketReader<ByteBuffer> {
  override suspend fun read(pool: MemoryPool, timeout: Long) = socket.read(pool, timeout)
  override fun close() = socket.close()
}