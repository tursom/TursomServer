package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.io.Closeable

interface InputStream : Closeable {
  val available: Int get() = 0
  fun skip(n: Long)
  fun read(): Int
  fun read(buffer: ByteBuffer)
  fun read(
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

