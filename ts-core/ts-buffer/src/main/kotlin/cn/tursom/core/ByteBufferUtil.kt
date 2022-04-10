package cn.tursom.core

import cn.tursom.core.buffer.impl.DirectByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.nio.ByteBuffer

/**
 * hack java.nio.HeapByteBuffer
 */
object ByteBufferUtil {
  private val field = ByteBuffer::class.java.getDeclaredField("offset")
  private val bufferWrapper = ArrayList<(ByteBuffer, Boolean) -> cn.tursom.core.buffer.ByteBuffer?>()
  val empty: cn.tursom.core.buffer.ByteBuffer = HeapByteBuffer(0)

  init {
    field.isAccessible = true
    addWrapper { it, write ->
      if (!it.hasArray()) {
        null
      } else {
        HeapByteBuffer(it, write)
      }
    }

    addWrapper { it, write ->
      if (it.hasArray()) {
        null
      } else {
        DirectByteBuffer(it, write)
      }
    }
  }

  fun addWrapper(wrapper: (ByteBuffer, Boolean) -> cn.tursom.core.buffer.ByteBuffer?) {
    bufferWrapper.add(wrapper)
  }

  fun wrap(byteBuffer: ByteBuffer, write: Boolean = true): cn.tursom.core.buffer.ByteBuffer {
    bufferWrapper.forEach { wrapper ->
      return wrapper(byteBuffer, write) ?: return@forEach
    }
    val buffer = HeapByteBuffer(byteBuffer.limit() - byteBuffer.position())
    if (!write) {
      buffer.writeBuffer {
        it.put(byteBuffer)
      }
    }
    return buffer
  }

  fun wrap(array: ByteArray, offset: Int = 0, size: Int = array.size - offset): ByteBuffer {
    val buffer = ByteBuffer.wrap(array, 0, offset + size)
    if (offset > 0) field.set(buffer, offset)
    return buffer
  }

  fun wrap(string: String) = wrap(string.toByteArray())
}