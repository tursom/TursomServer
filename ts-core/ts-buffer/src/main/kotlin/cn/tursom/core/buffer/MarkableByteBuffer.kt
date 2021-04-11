package cn.tursom.buffer

import cn.tursom.core.buffer.ByteBuffer

interface MarkableByteBuffer : ByteBuffer {
  fun mark()
  fun resume()
}