package cn.tursom.socket

import cn.tursom.core.pool.MemoryPool

class BufferedNioSocket(
  override val prevChannel: AsyncSocket,
  override val pool: MemoryPool
) : BufferedAsyncSocket, AsyncSocket by prevChannel