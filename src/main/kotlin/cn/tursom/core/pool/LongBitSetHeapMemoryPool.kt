package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer

class LongBitSetHeapMemoryPool (
  blockSize: Int,
  emptyPoolBuffer: (blockSize: Int) -> ByteBuffer = ::HeapByteBuffer
) : LongBitSetAbstractMemoryPool(blockSize, emptyPoolBuffer, HeapByteBuffer(64 * blockSize)) {
  override fun toString(): String {
    return "LongBitSetDirectMemoryPool(blockSize=$blockSize, blockCount=$blockCount, allocated=$allocated)"
  }
}