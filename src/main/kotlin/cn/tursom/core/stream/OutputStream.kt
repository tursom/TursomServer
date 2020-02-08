package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import java.io.Closeable

interface OutputStream : Closeable {
  fun write(byte: Byte)
  fun write(buffer: ByteArray)
  fun write(buffer: ByteArray, offset: Int, len: Int)
  fun write(buffer: ByteBuffer)
  fun flush()
}

