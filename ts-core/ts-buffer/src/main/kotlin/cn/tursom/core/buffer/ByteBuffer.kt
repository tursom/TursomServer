package cn.tursom.core.buffer

import java.io.Closeable

/**
 * 针对其他库的字节缓冲的封装
 */
@Suppress("unused")
interface ByteBuffer : Closeable, ReadableByteBuffer, WriteableByteBuffer {
  fun <T> getExtension(key: ByteBufferExtensionKey<T>): T? = key.get(this)

  override fun close() {}
  fun closeChild(child: ByteBuffer) {}

  fun slice(position: Int, size: Int): ByteBuffer = slice(position, size, 0, 0)
  fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer

  /**
   * @return 底层 nio buffer 是否已更新
   */
  fun resize(newSize: Int): Boolean

  fun split(maxSize: Int): Array<ByteBuffer> {
    val size = (((capacity - 1) / maxSize) + 1).and(0x7fff_ffff)
    return Array(size) {
      if (it != size - 1) {
        slice(it * maxSize, maxSize)
      } else {
        slice(it * maxSize, capacity - it * maxSize)
      }
    }
  }

  fun readAllSize(): Int {
    val size = readable
    readPosition += size
    return size
  }
}