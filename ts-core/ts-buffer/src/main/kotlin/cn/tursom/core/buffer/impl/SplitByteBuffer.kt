package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.ClosedBufferException
import cn.tursom.core.buffer.ProxyByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class SplitByteBuffer(
  val parent: ByteBuffer,
  private val childCount: AtomicInteger,
  override val agent: ByteBuffer,
) : ProxyByteBuffer, ByteBuffer by agent {
  init {
    childCount.incrementAndGet()
  }

  private val atomicClosed = AtomicBoolean(false)

  override val closed: Boolean get() = atomicClosed.get()

  override fun close() {
    if (atomicClosed.compareAndSet(false, true)) {
      agent.close()
      parent.closeChild(this)
    }
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    if (closed) {
      throw ClosedBufferException("SplitByteBuffer was closed.")
    }
    return SplitByteBuffer(parent, childCount, agent.slice(position, size, readPosition, writePosition))
  }

  override fun resize(newSize: Int): Boolean {
    val successful = agent.resize(newSize)
    if (successful) {
      close()
    }
    return successful
  }

  protected fun finalize() {
    close()
  }

  override fun toString(): String {
    return "SplitByteBuffer(parent=$parent, childCount=$childCount, agent=$agent)"
  }
}