package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.ListByteBuffer
import cn.tursom.core.pool.ExpandableMemoryPool
import cn.tursom.core.pool.LongBitSetDirectMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.util.CurrentTimeMillisClock

class LengthFieldBasedFrameReader(
  private val prevReader: ChannelReader<ByteBuffer>,
) : ChannelReader<ByteBuffer> {
  private var lastRead: ByteBuffer? = null

  override suspend fun read(pool: MemoryPool, timeout: Long): ByteBuffer {
    val startTime = CurrentTimeMillisClock.now
    val maxSize = prevReader.read(prevPool, timeout).let {
      val size = it.getInt()
      it.close()
      size
    }
    val bufList = ListByteBuffer()
    var readSize = 0
    lastRead?.let { buffer ->
      bufList.append(buffer)
      readSize += buffer.readable
    }
    while (readSize < maxSize) {
      val buffer = prevReader.read(pool, timeout - (CurrentTimeMillisClock.now - startTime))
      readSize += buffer.readable
      bufList.append(
        if (readSize > maxSize) {
          lastRead = buffer.slice(
            buffer.readPosition + readSize - maxSize,
            buffer.capacity - readSize - maxSize,
            0,
            buffer.capacity - readSize - maxSize
          )
          buffer.slice(
            buffer.readPosition,
            readSize - maxSize,
            0,
            readSize - maxSize
          )
        } else {
          buffer
        }
      )
    }
    return bufList
  }

  override fun close() = prevReader.close()

  companion object {
    private val prevPool = ExpandableMemoryPool { LongBitSetDirectMemoryPool(4) }
  }
}