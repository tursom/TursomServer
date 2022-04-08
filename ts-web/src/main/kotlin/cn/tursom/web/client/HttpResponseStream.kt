package cn.tursom.web.client

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.io.Closeable

interface HttpResponseStream : Closeable {
  suspend fun skip(n: Long)
  suspend fun read(): Int
  suspend fun read(buffer: ByteBuffer)
  suspend fun read(
    buffer: ByteArray,
    offset: Int = 0,
    len: Int = buffer.size - offset,
  ): Int {
    val byteBuffer = HeapByteBuffer(buffer, offset, len)
    byteBuffer.clear()
    read(byteBuffer)
    return byteBuffer.writePosition
  }
}
