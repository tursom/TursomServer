package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.socket.AsyncSocket
import cn.tursom.socket.enhance.SocketWriter

class SocketWriterImpl(
  private val socket: AsyncSocket
) : SocketWriter<ByteBuffer> {
  private val bufList = ArrayList<ByteBuffer>(4)
  override suspend fun write(value: ByteBuffer) {
    bufList.add(value)
  }

  override suspend fun flush(timeout: Long): Long {
    val read = when (bufList.size) {
      0 -> 0
      1 -> socket.write(bufList[0], timeout).toLong()
      else -> socket.write(bufList.toTypedArray(), timeout)
    }
    bufList.clear()
    return read
  }

  override fun close() {
    socket.close()
  }
}