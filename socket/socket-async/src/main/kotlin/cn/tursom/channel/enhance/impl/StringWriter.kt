package cn.tursom.channel.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.channel.enhance.SocketWriter

class StringWriter(
  val prevWriter: SocketWriter<ByteBuffer>
) : SocketWriter<String> {
  constructor(socket: IAsyncNioSocket) : this(LengthFieldPrependWriter(socket))

  override suspend fun put(value: String) {
    val buf = HeapByteBuffer(value.toByteArray())
    buf.writePosition = buf.capacity
    prevWriter.put(buf)
  }

  override suspend fun flush(timeout: Long) {
    prevWriter.flush(timeout)
  }

  override fun close() {
    prevWriter.close()
  }
}