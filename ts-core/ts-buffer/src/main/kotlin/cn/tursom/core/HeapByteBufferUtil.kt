package cn.tursom.core

import java.nio.ByteBuffer

/**
 * hack java.nio.HeapByteBuffer
 */
object HeapByteBufferUtil {
  private val field = ByteBuffer::class.java.getDeclaredField("offset")

  init {
    field.isAccessible = true
  }

  fun wrap(array: ByteArray, offset: Int = 0, size: Int = array.size - offset): ByteBuffer {
    val buffer = ByteBuffer.wrap(array, 0, offset + size)
    if (offset > 0) field.set(buffer, offset)
    return buffer
  }

  fun wrap(string: String) = wrap(string.toByteArray())
}