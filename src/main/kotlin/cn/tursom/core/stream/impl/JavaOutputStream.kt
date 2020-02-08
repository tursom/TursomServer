package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.OutputStream

class JavaOutputStream(
  @Suppress("MemberVisibilityCanBePrivate") val outputStream: java.io.OutputStream
) : OutputStream {
  override fun write(byte: Byte) {
    outputStream.write(byte.toInt())
  }

  override fun write(buffer: ByteArray) {
    outputStream.write(buffer)
  }

  override fun write(buffer: ByteArray, offset: Int, len: Int) {
    outputStream.write(buffer, offset, len)
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