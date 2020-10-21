package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.impl.PooledByteBuffer
import cn.tursom.core.datastruct.AtomicBitSet

abstract class AbstractMemoryPool(
  val blockSize: Int,
  val blockCount: Int,
  val emptyPoolBuffer: (blockSize: Int) -> ByteBuffer = ::HeapByteBuffer,
  private val memoryPool: ByteBuffer,
) : MemoryPool {
  private val bitMap = AtomicBitSet(blockCount.toLong())
  val allocated: Int get() = bitMap.trueCount.toInt()

  private fun getMemory(token: Int): ByteBuffer = synchronized(this) {
    PooledByteBuffer(memoryPool.slice(token * blockSize, blockSize), this, token)
  }

  /**
   * @return token
   */
  private fun allocate(): Int {
    var index = bitMap.firstDown()
    while (index in 0 until blockCount) {
      if (bitMap.up(index)) {
        return index.toInt()
      }
      index = if (bitMap[index]) bitMap.firstDown() else index
    }
    return -1
  }

  override fun free(memory: ByteBuffer) {
    if (memory is PooledByteBuffer && memory.pool == this) {
      free(memory.token)
    }
  }

  override fun free(token: Int) {
    @Suppress("ControlFlowWithEmptyBody")
    if (token in 0 until blockCount) while (!bitMap.down(token.toLong()));
  }

  override fun getMemoryOrNull(): ByteBuffer? {
    val token = allocate()
    return if (token in 0 until blockCount) {
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