package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.ProxyByteBuffer
import cn.tursom.core.pool.MemoryPool
import java.util.concurrent.atomic.AtomicInteger

class InstantByteBuffer(
  override val agent: ByteBuffer,
  val pool: MemoryPool,
) : ProxyByteBuffer, ByteBuffer by agent {
  override var closed = false
  private val childCount = AtomicInteger(0)

  override fun close() {
    if (childCount.get() == 0) {
      agent.close()
      pool.free(this)
      closed = true
    }
  }

  override fun closeChild(child: ByteBuffer) {
    if (child is SplitByteBuffer && child.parent == this && childCount.decrementAndGet() == 0) {
      if (closed) {
        close()
      }
    }
  }

  override fun slice(position: Int, size: Int): ByteBuffer {
    return SplitByteBuffer(this, childCount, agent.slice(position, size))
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    return SplitByteBuffer(this, childCount, agent.slice(position, size, readPosition, writePosition))
  }

  override fun toString() = "InstantByteBuffer(agent=$agent)"
}