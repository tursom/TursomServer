package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.ExpandableMemoryPool
import cn.tursom.core.pool.LongBitSetDirectMemoryPool

class LengthFieldPrependWriter(
  private val prevWriter: ChannelWriter<ByteBuffer>
) : ChannelWriter<ByteBuffer> {
  private val bufList = ArrayList<ByteBuffer>(4)

  override suspend fun write(value: ByteBuffer) {
    bufList.add(value)
  }

  override suspend fun flush(timeout: Long): Long {
    var size = 0
    bufList.forEach { size += it.readable }
    val sizeBuffer = prevPool.getMemory()
    sizeBuffer.put(size)

    prevWriter.write(sizeBuffer)
    prevWriter.write(bufList)
    val flush = prevWriter.flush(timeout)

    bufList.clear()
    sizeBuffer.close()
    return flush
  }

  override fun close() {
    prevWriter.close()
  }

  companion object {
    private val prevPool = ExpandableMemoryPool { LongBitSetDirectMemoryPool(4) }
  }
}