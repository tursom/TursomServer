package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.PooledByteBuffer
import java.io.Closeable

/**
 * 可以记录与释放分配内存的内存池
 * 在被垃圾回收时能自动回收内存池的内存
 * 是一次性用品
 * 非线程安全
 */
class MarkedMemoryPool(private val pool: MemoryPool) : MemoryPool by pool, Closeable {
  private val allocatedList = ArrayList<ByteBuffer>(2)
  override fun getMemory(): ByteBuffer {
    val memory = pool.getMemory()
    allocatedList.add(memory)
    return memory
  }

  override fun getMemoryOrNull(): ByteBuffer? {
    val memory = pool.getMemoryOrNull()
    if (memory != null) allocatedList.add(memory)
    return memory
  }

  override fun close() {
    allocatedList.forEach(ByteBuffer::close)
    allocatedList.clear()
  }

  override fun gc() {
    pool.gc()
  }

  override fun toString(): String {
    val allocated = ArrayList<Int>(allocatedList.size)
    allocatedList.forEach {
      if (it is PooledByteBuffer && !it.closed) allocated.add(it.token)
    }
    return "MarkedMemoryPool(pool=$pool, allocated=$allocated)"
  }

  protected fun finalize() {
    close()
  }
}