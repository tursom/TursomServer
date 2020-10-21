package cn.tursom.core.pool

import cn.tursom.core.datastruct.LongBitSet
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.impl.PooledByteBuffer

/**
 * 无锁，固定容量的内存池
 */
abstract class LongBitSetAbstractMemoryPool(
  val blockSize: Int,
  val emptyPoolBuffer: (blockSize: Int) -> ByteBuffer = ::HeapByteBuffer,
  private val memoryPool: ByteBuffer,
) : MemoryPool {
  private val bitMap = LongBitSet()
  val allocated: Int get() = bitMap.trueCount.toInt()
  val blockCount: Int get() = 64

  init {
    assert(memoryPool.capacity >= 64 * blockSize)
  }

  private fun getMemory(token: Int): ByteBuffer = synchronized(this) {
    return PooledByteBuffer(memoryPool.slice(token * blockSize, blockSize), this, token)
  }

  /**
   * @return token
   */
  private fun allocate(): Int {
    var index = bitMap.firstDown()
    while (index >= 0) {
      if (bitMap.up(index)) {
        return index
      }
      index = bitMap.firstDown()
    }
    return index
  }

  override fun free(memory: ByteBuffer) {
    if (memory is PooledByteBuffer && memory.pool == this) {
      free(memory.token)
    }
  }

  override fun free(token: Int) {
    @Suppress("ControlFlowWithEmptyBody")
    if (token >= 0) while (!bitMap.down(token));
  }

  override fun getMemoryOrNull(): ByteBuffer? {
    val token = allocate()
    return if (token >= 0) {
      return getMemory(token)
    } else {
      null
    }
  }

  override fun getMemory(): ByteBuffer = getMemoryOrNull() ?: emptyPoolBuffer(blockSize)

  override fun get(blockCount: Int): Array<ByteBuffer> = Array(blockCount) {
    val token = allocate()
    if (token in 0 until blockCount) {
      getMemory(token)
    } else {
      emptyPoolBuffer(blockSize)
    }
  }
}