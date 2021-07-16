package cn.tursom.core.buffer

interface ByteBufferExtensionKey<T> {
  fun get(buffer: ByteBuffer): T? = null
}
