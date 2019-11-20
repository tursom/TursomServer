package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.buffer.ProxyByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class SplitByteBuffer(
    private val parent: ByteBuffer,
    private val childCount: AtomicInteger,
    override val agent: ByteBuffer
) : ProxyByteBuffer, ByteBuffer by agent {
  init {
    childCount.incrementAndGet()
  }

  private val atomicClosed = AtomicBoolean(false)

  override val closed: Boolean get() = atomicClosed.get()

  override fun close() {
    if (atomicClosed.compareAndSet(false, true)) {
      agent.close()
      childCount.decrementAndGet()
      if (childCount.get() == 0 && (parent.closed || parent.resized)) {
        parent.close()
      }
    }
  }

  override fun slice(offset: Int, size: Int): ByteBuffer {
    return SplitByteBuffer(parent, childCount, agent.slice(offset, size))
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
}