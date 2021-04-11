package cn.tursom.channel

import cn.tursom.core.pool.MemoryPool

class BufferedAsyncChannelImpl(override val pool: MemoryPool, override val prevChannel: AsyncChannel) :
  BufferedAsyncChannel,
  AsyncChannel by prevChannel