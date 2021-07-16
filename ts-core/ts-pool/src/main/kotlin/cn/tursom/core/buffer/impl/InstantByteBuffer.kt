package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.ClosedBufferException
import cn.tursom.core.buffer.ProxyByteBuffer
import cn.tursom.core.pool.MemoryPool
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class InstantByteBuffer(
  override val agent: ByteBuffer,
  val pool: MemoryPool,
) : ProxyByteBuffer, ByteBuffer by agent {
  override val closed get() = aClosed.get()
  private val childCount = AtomicInteger(0)
  private val aClosed = AtomicBoolean(false)

  override fun close() {
    if (childCount.get() == 0 && aClosed.compareAndSet(false, true)) {
      agent.close()
      pool.free(this)
    }
  }

  override fun closeChild(child: ByteBuffer) {
    if (child is SplitByteBuffer && child.parent == this && childCount.decrementAndGet() == 0) {
      close()
    }
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    if (closed) {
      throw ClosedBufferException("InstantByteBuffer was closed.")
    }
    return SplitByteBuffer(this, childCount, agent.slice(position, size, readPosition, writePosition))
  }

  override fun toString() = "InstantByteBuffer(agent=$agent)"
}