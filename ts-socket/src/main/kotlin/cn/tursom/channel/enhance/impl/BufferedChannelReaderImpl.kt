package cn.tursom.channel.enhance.impl

import cn.tursom.channel.BufferedAsyncChannel
import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool

class BufferedChannelReaderImpl(
  private val socket: BufferedAsyncChannel,
) : ChannelReader<ByteBuffer> {
  suspend fun read(timeout: Long) = socket.read(socket.pool, timeout)
  override suspend fun read(pool: MemoryPool, timeout: Long) = socket.read(pool, timeout)
  override fun close() = socket.close()
}