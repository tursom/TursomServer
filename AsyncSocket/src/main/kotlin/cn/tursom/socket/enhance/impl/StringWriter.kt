package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.socket.enhance.SocketWriter

class StringWriter(
  private val prevWriter: SocketWriter<ByteBuffer>
) : SocketWriter<String> {
  private val stringList = ArrayList<String>()
  override suspend fun write(value: String) {
    stringList.add(value)
  }

  override suspend fun flush(timeout: Long): Long {
    stringList.forEach { prevWriter.write(HeapByteBuffer(it.toByteArray())) }
    stringList.clear()
    return prevWriter.flush(timeout)
  }

  override fun close() {
    prevWriter.close()
  }
}