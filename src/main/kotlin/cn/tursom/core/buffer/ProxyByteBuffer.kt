package cn.tursom.buffer

import cn.tursom.core.buffer.ByteBuffer

interface ProxyByteBuffer : ByteBuffer {
  val agent: ByteBuffer
}