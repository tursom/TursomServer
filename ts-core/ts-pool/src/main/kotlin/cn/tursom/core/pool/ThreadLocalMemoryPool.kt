package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer

class ThreadLocalMemoryPool(
  private val poolFactory: () -> MemoryPool,
) : MemoryPool {
  private val threadLocal = ThreadLocal<MemoryPool>()

  override fun free(memory: ByteBuffer) = throw NotImplementedError("ThreadLocalMemoryPool won't allocate any memory")

  override fun getMemory(): ByteBuffer = getPool().getMemory()

  override fun getMemoryOrNull(): ByteBuffer? = getPool().getMemoryOrNull()

  override fun toString(): String {
    return "ThreadLocalMemoryPool(threadLocal=$threadLocal)"
  }

  override fun gc() {
    threadLocal.get()?.gc()
  }

  private fun getPool(): MemoryPool {
    var pool = threadLocal.get()
    if (pool == null) {
      pool = poolFactory()
      threadLocal.set(pool)
    }
    return pool
  }
}