package cn.tursom.core.buffer

interface ProxyByteBuffer : ByteBuffer {
  val agent: ByteBuffer
}