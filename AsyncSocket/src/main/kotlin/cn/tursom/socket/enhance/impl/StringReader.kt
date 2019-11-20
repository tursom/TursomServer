package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.enhance.SocketReader

class StringReader(
  private val prevReader: SocketReader<ByteBuffer>
) : SocketReader<String> {
  override suspend fun read(pool: MemoryPool, timeout: Long) = prevReader.read(pool, timeout).let { buffer ->
    val string = buffer.getString()
    buffer.close()
    string
  }

  override fun close() = prevReader.close()
}