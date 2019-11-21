package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.buffer.ProxyByteBuffer
import cn.tursom.core.pool.MemoryPool
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * 在被垃圾回收时能保证释放占用的内存池内存
 */
class PooledByteBuffer(
  override val agent: ByteBuffer,
  val pool: MemoryPool,
  val token: Int
) : ProxyByteBuffer, ByteBuffer by agent {
  /**
   * 这个变量保证 buffer 不会被释放多次
   */
  private val open = AtomicBoolean(true)
  private val childCount = AtomicInteger(0)
  override val resized get() = agent.resized

  override val closed: Boolean get() = !open.get() && !resized
  override fun close() {
    if (childCount.get() == 0) {
      if (open.compareAndSet(true, false)) {
        pool.free(this)
      }
    }
  }

  override fun resize(newSize: Int): Boolean {
    val successful = agent.resize(newSize)
    if (successful) {
      close()
    }
    return successful
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    return SplitByteBuffer(this, childCount, agent.slice(position, size, readPosition, writePosition))
  }

  override fun toString(): String {
    return "PooledByteBuffer(buffer=$agent, pool=$pool, token=$token, open=$open)"
  }

  protected fun finalize() {
    pool.free(this)
  }
}