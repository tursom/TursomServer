package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.ProxyByteBuffer
import cn.tursom.core.buffer.ClosedBufferException
import cn.tursom.core.pool.MemoryPool
import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * 在被垃圾回收时能保证释放占用的内存池内存
 */
class PooledByteBuffer(
  agent: ByteBuffer,
  val pool: MemoryPool,
  val token: Int,
  autoClose: Boolean = false,
) : ProxyByteBuffer, CloseSafeByteBuffer(agent) {
  private val reference = if (autoClose) PhantomReference(this, allocatedReferenceQueue) else null

  init {
    if (reference != null) allocatedMap[reference] = pool to token
  }

  private val childCount = AtomicInteger(0)
  override val resized get() = agent.resized

  override fun close() {
    if (tryClose()) {
      if (childCount.get() == 0) {
        if (reference != null) allocatedMap.remove(reference)
        pool.free(this)
      }
    }
  }

  override fun resize(newSize: Int): Boolean {
    if (closed) {
      return false
    }
    val successful = agent.resize(newSize)
    if (successful) {
      close()
    }
    return successful
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    if (closed) {
      throw ClosedBufferException("PooledByteBuffer has closed.")
    }
    return SplitByteBuffer(this, childCount, agent.slice(position, size, readPosition, writePosition))
  }


  override fun toString(): String {
    return "PooledByteBuffer(buffer=$agent, pool=$pool, token=$token, closed=$closed)"
  }

  //protected fun finalize() {
  //  pool.free(this)
  //}

  companion object {
    private val allocatedReferenceQueue = ReferenceQueue<PooledByteBuffer>()
    private val allocatedMap = ConcurrentHashMap<Reference<PooledByteBuffer>, Pair<MemoryPool, Int>>()

    init {
      thread(isDaemon = true) {
        while (true) {
          val (pool, token) = allocatedMap.remove(allocatedReferenceQueue.remove() ?: return@thread) ?: continue
          try {
            pool.free(token)
          } catch (e: Exception) {
          }
        }
      }
    }
  }
}