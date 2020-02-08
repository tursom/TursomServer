package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import java.io.Closeable

interface InputStream : Closeable {
  val available: Int get() = 0
  fun skip(n: Long)
  fun read(): Int
  fun read(buffer: ByteArray)
  fun read(buffer: ByteArray, offset: Int, len: Int)
  fun read(buffer: ByteBuffer)
}

