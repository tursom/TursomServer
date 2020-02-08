package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.InputStream

class JavaInputStream(
  @Suppress("MemberVisibilityCanBePrivate") val inputStream: java.io.InputStream
) : InputStream {
  override val available: Int get() = inputStream.available()
  override fun skip(n: Long) {
    inputStream.skip(n)
  }

  override fun read(): Int = inputStream.read()

  override fun read(buffer: ByteArray) {
    inputStream.read(buffer)
  }

  override fun read(buffer: ByteArray, offset: Int, len: Int) {
    inputStream.read(buffer, offset, len)
  }

  override fun read(buffer: ByteBuffer) {
    buffer.put(inputStream)
  }

  override fun close() {
    inputStream.close()
  }
}