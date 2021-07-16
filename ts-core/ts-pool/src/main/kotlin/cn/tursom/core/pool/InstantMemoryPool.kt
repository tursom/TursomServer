package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.impl.InstantByteBuffer
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentLinkedQueue

class InstantMemoryPool(
  val blockSize: Int,
  val newMemory: (blockSize: Int) -> ByteBuffer = ::HeapByteBuffer
) : MemoryPool {
  private val memoryList = ConcurrentLinkedQueue<SoftReference<InstantByteBuffer>>()

  override fun free(memory: ByteBuffer) {
    if (memory is InstantByteBuffer && memory.pool == this && !memory.closed) {
      memoryList.add(SoftReference(memory))
    }
  }

  override fun getMemory(): ByteBuffer = getMemoryOrNull() ?: InstantByteBuffer(newMemory(blockSize), this)

  override fun getMemoryOrNull(): ByteBuffer? {
    memoryList.forEach {
      return it.get() ?: return@forEach
    }
    return null
  }

  override fun toString(): String {
    return "InstantMemoryPool(blockSize=$blockSize, memoryList=${memoryList.size})"
  }
}