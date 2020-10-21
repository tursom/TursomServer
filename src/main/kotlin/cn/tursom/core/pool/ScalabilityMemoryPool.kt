package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 利用 GC 进行内存释放的内存池
 */
class ScalabilityMemoryPool(private val poolFactory: () -> MemoryPool) : MemoryPool {
  private val poolList = ConcurrentLinkedQueue<SoftReference<MemoryPool>>()
  @Volatile
  private var usingPool: MemoryPool
  private val poolLock = AtomicBoolean(false)

  init {
    usingPool = poolFactory()
    poolList.add(SoftReference(usingPool))
  }

  /**
   * 释放所有被 gc 释放的内存
   */
  override fun gc() {
    val iterator = poolList.iterator()
    iterator.forEach {
      if (it.get() == null) {
        iterator.remove()
      }
    }
  }

  override fun free(memory: ByteBuffer) = Unit

  override fun getMemory(): ByteBuffer {
    var buffer = usingPool.getMemoryOrNull()
    if (buffer != null) return buffer
    val iterator = poolList.iterator()
    iterator.forEach {
      val pool = it.get() ?: run {
        iterator.remove()
        return@forEach
      }
      buffer = pool.getMemoryOrNull()
      if (buffer != null) usingPool = pool
      return buffer ?: return@forEach
    }
    return newPool()
  }

  override fun getMemoryOrNull(): ByteBuffer = getMemory()

  override fun toString(): String {
    return "ScalabilityMemoryPool(poolList=$poolList, usingPool=$usingPool)"
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  private fun newPool(): ByteBuffer {
    return if (poolLock.compareAndSet(false, true)) {
      val newPool = poolFactory()
      poolList.add(SoftReference(newPool))
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
      usingPool.getMemory()
    }
  }
}