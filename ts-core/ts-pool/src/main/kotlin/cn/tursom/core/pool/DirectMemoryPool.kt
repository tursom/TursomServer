package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.DirectByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer


class DirectMemoryPool(
  blockSize: Int = 1024,
  blockCount: Int = 16,
  emptyPoolBuffer: (blockSize: Int) -> ByteBuffer = ::HeapByteBuffer,
) : AbstractMemoryPool(
  blockSize,
  blockCount,
  emptyPoolBuffer,
  DirectByteBuffer(java.nio.ByteBuffer.allocateDirect(blockSize * blockCount))
) {
  override fun toString(): String {
    return "DirectMemoryPool(blockSize=$blockSize, blockCount=$blockCount, allocated=$allocated)"
  }
}