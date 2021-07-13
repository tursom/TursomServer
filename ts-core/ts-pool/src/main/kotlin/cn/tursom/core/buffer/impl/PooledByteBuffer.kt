package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.ClosedBufferException
import cn.tursom.core.buffer.ProxyByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.reference.FreeReference
import java.util.concurrent.atomic.AtomicInteger

class PooledByteBuffer(
  agent: ByteBuffer,
  val pool: MemoryPool,
  val token: Int,
  autoClose: Boolean = false,
) : ProxyByteBuffer, CloseSafeByteBuffer(agent) {
  class AutoFreeReference(
    pooledByteBuffer: PooledByteBuffer,
    val pool: MemoryPool,
    val token: Int,
  ) : FreeReference<PooledByteBuffer>(pooledByteBuffer) {
    override fun release() {
      pool.free(token)
    }
  }

  private val reference = if (autoClose) AutoFreeReference(this, pool, token) else null

  private val childCount = AtomicInteger(0)
  override val resized get() = agent.resized

  override fun close() {
    if (tryClose()) {
      if (childCount.get() == 0) {
        reference?.cancel()
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
}