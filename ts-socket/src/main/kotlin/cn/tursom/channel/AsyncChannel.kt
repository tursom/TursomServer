package cn.tursom.channel

import cn.tursom.core.pool.MemoryPool
import java.io.Closeable
import java.net.SocketAddress

interface AsyncChannel : Closeable, WritableAsyncChannel, ReadableAsyncChannel {
  val open: Boolean
  val remoteAddress: SocketAddress
  fun getBuffed(pool: MemoryPool): BufferedAsyncChannel = BufferedAsyncChannelImpl(pool, this)

  companion object {
    const val emptyBufferCode = 0
    const val emptyBufferLongCode = 0L
  }
}
