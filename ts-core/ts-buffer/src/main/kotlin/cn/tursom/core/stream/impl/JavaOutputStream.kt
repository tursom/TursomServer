package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.OutputStream

class JavaOutputStream(
  @Suppress("MemberVisibilityCanBePrivate") val outputStream: java.io.OutputStream
) : OutputStream {
  override fun write(byte: Byte) {
    outputStream.write(byte.toInt())
  }

  override fun write(buffer: ByteArray, offset: Int, len: Int): Int {
    outputStream.write(buffer, offset, len)
    return len
  }

  override fun write(buffer: ByteBuffer) {
    buffer.writeTo(outputStream)
  }

  override fun flush() {
    outputStream.flush()
  }

  override fun close() {
    outputStream.close()
  }
}