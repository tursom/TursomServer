package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.io.Closeable

interface OutputStream : Closeable {
  fun write(buffer: ByteBuffer)
  fun write(byte: Byte) {
    write(byteArrayOf(byte))
  }

  fun write(
    buffer: ByteArray,
    offset: Int = 0,
    len: Int = buffer.size - offset
  ): Int {
    val byteBuffer = HeapByteBuffer(buffer, offset, len)
    write(byteBuffer)
    return byteBuffer.readPosition
  }

  fun flush() {}
}

