package cn.tursom.core.stream.impl

import cn.tursom.core.stream.IOStream
import cn.tursom.core.stream.InputStream
import cn.tursom.core.stream.OutputStream

class PairIOStream(
  @Suppress("MemberVisibilityCanBePrivate")
  val inputStream: JavaInputStream,
  @Suppress("MemberVisibilityCanBePrivate")
  val outputStream: JavaOutputStream,
) : IOStream, InputStream by inputStream, OutputStream by outputStream {
  constructor(
    outputStream: JavaOutputStream,
    inputStream: JavaInputStream,
  ) : this(inputStream, outputStream)

  override fun close() {
    inputStream.close()
    outputStream.close()
  }
}
