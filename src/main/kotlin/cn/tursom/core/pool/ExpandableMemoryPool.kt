package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 可自动申请新内存空间的内存池
 * 线程安全
 */
class ExpandableMemoryPool(private val poolFactory: () -> MemoryPool) : MemoryPool {
  private val poolList = ConcurrentLinkedQueue<MemoryPool>()
  @Volatile
  private var usingPool: MemoryPool
  private val poolLock = AtomicBoolean(false)

  init {
    usingPool = poolFactory()
    poolList.add(usingPool)
  }

  /**
   * 强制释放所有内存
   */
  override fun gc() {
    poolList.clear()
    usingPool = poolFactory()
    poolList.add(usingPool)
  }

  override fun free(memory: ByteBuffer) = throw NotImplementedError("ExpandableMemoryPool won't allocate any memory")

  override fun getMemory(): ByteBuffer {
    var buffer = usingPool.getMemoryOrNull()
    if (buffer != null) return buffer
    poolList.forEach {
      val pool = it ?: return@forEach
      buffer = pool.getMemoryOrNull()
      if (buffer != null) usingPool = pool
      return buffer ?: return@forEach
    }
    return newPool()
  }

  override fun getMemoryOrNull(): ByteBuffer = getMemory()

  override fun toString(): String {
    return "ExpandableMemoryPool(poolList=$poolList, usingPool=$usingPool)"
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  private fun newPool(): ByteBuffer {
    return if (poolLock.compareAndSet(false, true)) {
      val newPool = poolFactory()
      poolList.add(newPool)
      poolLock.set(false)
      usingPool = newPool
      synchronized(poolLock) {
        (poolLock as Object).notifyAll()
      }
      newPool.getMemory()
    } else {
      synchronized(poolLock) {
        (poolLock as Object).wait(500)
      }
      poolList.last().getMemory()
    }
  }
}