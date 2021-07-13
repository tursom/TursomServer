package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer

interface SuspendOutputStream : OutputStream {
  suspend fun suspendWrite(buffer: ByteBuffer)
  suspend fun suspendWrite(byte: Byte) {
    suspendWrite(byteArrayOf(byte))
  }

  suspend fun suspendWrite(
    buffer: ByteArray,
    offset: Int = 0,
    len: Int = buffer.size - offset,
  ): Int {
    val byteBuffer = HeapByteBuffer(buffer, offset, len)
    suspendWrite(byteBuffer)
    return byteBuffer.readPosition
  }

  suspend fun suspendFlush() {}
}
