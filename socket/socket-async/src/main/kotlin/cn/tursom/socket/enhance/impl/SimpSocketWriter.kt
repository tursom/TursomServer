package cn.tursom.socket.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.SocketWriter
import java.util.concurrent.ConcurrentLinkedQueue

class SimpSocketWriter(
  val socket: IAsyncNioSocket
) : SocketWriter<ByteBuffer> {
  private val bufferQueue = ConcurrentLinkedQueue<ByteBuffer>()
  override suspend fun put(value: ByteBuffer) {
    bufferQueue.offer(value)
  }

  override suspend fun flush(timeout: Long) {
    val buffers = bufferQueue.toTypedArray()
    bufferQueue.clear()
    socket.write(buffers, timeout)
    buffers.forEach { it.close() }
  }

  override fun close() {
    socket.close()
  }
}