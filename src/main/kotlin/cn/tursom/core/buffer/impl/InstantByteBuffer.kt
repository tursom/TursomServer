package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.buffer.ProxyByteBuffer
import cn.tursom.core.pool.MemoryPool

class InstantByteBuffer(
    override val agent: ByteBuffer,
    val pool: MemoryPool
) : ProxyByteBuffer, ByteBuffer by agent {
  override var closed = false

  override fun close() {
    agent.close()
    pool.free(this)
    closed = true
  }

  override fun toString() = "InstantByteBuffer(agent=$agent)"
}