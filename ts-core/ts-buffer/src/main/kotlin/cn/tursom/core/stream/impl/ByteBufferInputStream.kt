package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.InputStream

class ByteBufferInputStream(
  val buffer: ByteBuffer
) : InputStream {
  private var closed = false
  override val available: Int get() = buffer.readable

  override fun skip(n: Long) {
    checkClosed {
      buffer.readPosition += n.toInt()
    }
  }

  override fun read(): Int = if (closed || buffer.readable == 0) -1 else buffer.get().toInt()

  override fun read(buffer: ByteArray, offset: Int, len: Int) = checkClosed {
    this.buffer.writeTo(buffer, offset, len)
  } ?: -1

  override fun read(buffer: ByteBuffer) {
    checkClosed {
      this.buffer.writeTo(buffer)
    }
  }

  private inline fun <T> checkClosed(action: () -> T): T? = if (!closed) action() else null

  override fun close() {
    closed = true
  }
}