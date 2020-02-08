package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.OutputStream

class ByteBufferOutputStream(
  val buffer: ByteBuffer
) : OutputStream {
  private var closed = false
  override fun write(byte: Byte) = checkClosed {
    buffer.put(byte)
  }

  override fun write(buffer: ByteArray) = checkClosed {
    this.buffer.put(buffer)
  }

  override fun write(buffer: ByteArray, offset: Int, len: Int) = checkClosed {
    this.buffer.put(buffer, offset, len)
  }

  override fun write(buffer: ByteBuffer) = checkClosed {
    buffer.writeTo(this.buffer)
  }

  override fun flush() {}

  private inline fun checkClosed(action: () -> Unit) {
    if (!closed) action()
  }

  override fun close() {
    closed = true
  }
}

