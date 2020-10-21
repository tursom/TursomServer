package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.DirectByteBuffer

class LongBitSetDirectMemoryPool(
  blockSize: Int,
  emptyPoolBuffer: (blockSize: Int) -> ByteBuffer = ::DirectByteBuffer
) : LongBitSetAbstractMemoryPool(blockSize, emptyPoolBuffer, DirectByteBuffer(64 * blockSize)) {
  override fun toString(): String {
    return "LongBitSetDirectMemoryPool(blockSize=$blockSize, blockCount=$blockCount, allocated=$allocated)"
  }
}