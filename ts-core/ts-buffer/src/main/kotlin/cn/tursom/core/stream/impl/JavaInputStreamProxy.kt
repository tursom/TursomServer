package cn.tursom.core.stream.impl

import java.io.InputStream

class JavaInputStreamProxy(
  val inputStream: cn.tursom.core.stream.InputStream,
) : InputStream() {
  override fun read(): Int = inputStream.read()
  override fun read(b: ByteArray, off: Int, len: Int): Int = inputStream.read(b, off, len)

  override fun skip(n: Long): Long {
    inputStream.skip(n)
    return n
  }
}