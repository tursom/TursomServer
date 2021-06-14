package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.ClosedBufferException
import cn.tursom.core.buffer.ProxyByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

open class CloseSafeByteBuffer(
  override val agent: ByteBuffer,
) : ByteBuffer by agent, ProxyByteBuffer {
  /**
   * 这个变量保证 buffer 不会被释放多次
   */
  private val open = AtomicBoolean(true)

  override val closed: Boolean
    get() = !open.get()

  fun tryClose() = open.compareAndSet(true, false)

  override fun readBuffer(): java.nio.ByteBuffer {
    if (closed) {
      throw ClosedBufferException("byte buffer has closed.")
    }
    return agent.readBuffer()
  }

  override fun writeBuffer(): java.nio.ByteBuffer {
    if (closed) {
      throw ClosedBufferException("byte buffer has closed.")
    }
    return agent.writeBuffer()
  }

  override val array: ByteArray
    get() {
      if (closed) {
        throw ClosedBufferException("byte buffer has closed.")
      }
      return agent.array
    }

  override fun reset() {
    if (closed) {
      throw ClosedBufferException("byte buffer has closed.")
    }
    return agent.reset()
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    if (closed) {
      throw ClosedBufferException("byte buffer has closed.")
    }
    return agent.slice(position, size, readPosition, writePosition)
  }

  override fun resize(newSize: Int): Boolean {
    if (closed) {
      throw ClosedBufferException("byte buffer has closed.")
    }
    return agent.resize(newSize)
  }
}
