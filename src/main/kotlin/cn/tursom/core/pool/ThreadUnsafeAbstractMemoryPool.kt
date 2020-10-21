package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.impl.PooledByteBuffer
import cn.tursom.core.datastruct.ArrayBitSet

abstract class ThreadUnsafeAbstractMemoryPool(
  val blockSize: Int,
  val blockCount: Int,
  val emptyPoolBuffer: (blockSize: Int) -> ByteBuffer = ::HeapByteBuffer,
  private val memoryPool: ByteBuffer,
  override var autoCollection: Boolean = false,
) : MemoryPool {
  private val bitMap = ArrayBitSet(blockCount.toLong())
  val allocated: Int get() = bitMap.trueCount.toInt()

  private fun unsafeAllocate(): Int {
    val index = bitMap.firstDown()
    return if (index in 0 until blockCount) {
      bitMap.up(index)
      index.toInt()
    } else {
      -1
    }
  }

  private fun unsafeFree(token: Int) {
    bitMap.down(token.toLong())
  }

  private fun unsafeGetMemory(token: Int): ByteBuffer {
    return PooledByteBuffer(memoryPool.slice(token * blockSize, blockSize), this, token,autoCollection)
  }

  /**
   * @return token
   */
  private fun allocate(): Int = unsafeAllocate()

  override fun free(memory: ByteBuffer) {
    if (memory is PooledByteBuffer && memory.pool == this) {
      val token = memory.token
      if (token in 0 until blockCount) unsafeFree(token)
    }
  }

  override fun getMemoryOrNull(): ByteBuffer? {
    val token = allocate()
    return if (token in 0 until blockCount) {
      unsafeGetMemory(token)
    } else {
      null
    }
  }

  override fun getMemory(): ByteBuffer = getMemoryOrNull() ?: emptyPoolBuffer(blockSize)

  override fun get(blockCount: Int): Array<ByteBuffer> = synchronized(this) {
    Array(blockCount) {
      val token = unsafeAllocate()
      if (token in 0 until blockCount) {
        unsafeGetMemory(token)
      } else {
        emptyPoolBuffer(blockSize)
      }
    }
  }
}