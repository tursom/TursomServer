package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.socket.enhance.SocketWriter

class ByteArrayWriter(
  private val prevWriter: SocketWriter<ByteBuffer>
) : SocketWriter<ByteArray> {
  override suspend fun write(value: ByteArray) {
    prevWriter.write(HeapByteBuffer(value))
  }

  override suspend fun flush(timeout: Long): Long = prevWriter.flush(timeout)
  override suspend fun writeAndFlush(value: ByteArray, timeout: Long): Long = prevWriter.writeAndFlush(HeapByteBuffer(value), timeout)
  override fun close() = prevWriter.close()
}