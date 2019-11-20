package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.enhance.SocketReader

class SingleListSocketReader(
  private val prevReader: SocketReader<ByteBuffer>
) : SocketReader<List<ByteBuffer>> {
  override suspend fun read(pool: MemoryPool, timeout: Long): List<ByteBuffer> = listOf(prevReader.read(pool, timeout))
  override fun close() = prevReader.close()
}