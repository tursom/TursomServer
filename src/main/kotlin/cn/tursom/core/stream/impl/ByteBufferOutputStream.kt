package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.OutputStream

class ByteBufferOutputStream(
  val buffer: ByteBuffer
) : OutputStream {
  private var closed = false
  override fun write(byte: Byte) {
    checkClosed {
      buffer.put(byte)
    }
  }

  override fun write(buffer: ByteArray, offset: Int, len: Int): Int = checkClosed {
    this.buffer.put(buffer, offset, len)
  } ?: -1

  override fun write(buffer: ByteBuffer) {
    checkClosed {
      buffer.writeTo(this.buffer)
    }
  }

  override fun flush() {}

  private inline fun <T> checkClosed(action: () -> T): T? = if (!closed) action() else null

  override fun close() {
    closed = true
  }
}

