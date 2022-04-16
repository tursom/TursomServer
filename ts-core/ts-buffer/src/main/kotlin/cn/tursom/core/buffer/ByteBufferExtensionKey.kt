package cn.tursom.core.buffer

interface ByteBufferExtensionKey<T> {
  val extensionClass: Class<T>

  fun get(buffer: ByteBuffer): T? = if (extensionClass.isInstance(buffer)) {
    @Suppress("UNCHECKED_CAST")
    buffer as T
  } else {
    null
  }
}
