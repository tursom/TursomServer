package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.readable
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.enhance.SocketReader

class ListStringReader(
  private val prevReader: SocketReader<Collection<ByteBuffer>>
) : SocketReader<String> {
  override suspend fun read(pool: MemoryPool, timeout: Long): String {
    val read = prevReader.read(pool, timeout)
    val size = read.readable
    val buffer = HeapByteBuffer(size)
    read.forEach {
      buffer.put(it)
      buffer.close()
    }
    return buffer.getString()
  }

  override fun close() = prevReader.close()
}