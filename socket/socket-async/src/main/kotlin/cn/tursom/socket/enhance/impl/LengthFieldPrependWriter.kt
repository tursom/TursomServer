package cn.tursom.socket.enhance.impl

import cn.tursom.buffer.MultipleByteBuffer
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.ArrayByteBuffer
import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.SocketWriter
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.ExpandableMemoryPool


class LengthFieldPrependWriter(
  val prevWriter: SocketWriter<ByteBuffer>
) : SocketWriter<ByteBuffer> {
  constructor(socket: IAsyncNioSocket) : this(SimpSocketWriter(socket))

  override suspend fun put(value: ByteBuffer) {
    val buffer = directMemoryPool.getMemory()
    buffer.put(value.readable)
    prevWriter.put(buffer)
    prevWriter.put(value)
    buffer.close()
  }

  override suspend fun flush(timeout: Long) {
    prevWriter.flush()
  }

  override fun close() {
    prevWriter.close()
  }

  companion object {
    @JvmStatic
    private val directMemoryPool = ExpandableMemoryPool { DirectMemoryPool(4, 64) }
  }
}

