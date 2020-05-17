package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.IOStream
import cn.tursom.core.stream.InputStream
import cn.tursom.core.stream.OutputStream
import cn.tursom.core.stream.SuspendInputStream

class ByteBufferIOStream private constructor(
  private val buffer: ByteBuffer,
  val inputStream: ByteBufferInputStream,
  val outputStream: ByteBufferOutputStream
) : IOStream,
  SuspendInputStream,
  InputStream by inputStream,
  OutputStream by outputStream {
  constructor(buffer: ByteBuffer) : this(buffer, ByteBufferInputStream(buffer), ByteBufferOutputStream(buffer))

  override fun skip(n: Long, handler: () -> Unit) = handler()
  override fun skip(n: Long) {}

  override fun read(handler: (Int) -> Unit) {
    handler(read())
  }

  override fun read(buffer: ByteArray, handler: (Int) -> Unit) {
    handler(read(buffer))
  }

  override fun read(buffer: ByteArray, offset: Int, len: Int, handler: (Int) -> Unit) {
    handler(read(buffer, offset, len))
  }

  override fun read(buffer: ByteBuffer, handler: () -> Unit) {
    read(buffer)
    handler()
  }

  override fun close() {
    buffer.close()
    inputStream.close()
    outputStream.close()
  }
}