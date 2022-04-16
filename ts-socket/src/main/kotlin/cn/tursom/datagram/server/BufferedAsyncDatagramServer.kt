package cn.tursom.datagram.server

import cn.tursom.core.pool.MemoryPool

class BufferedAsyncDatagramServer(
  port: Int,
  private val memoryPool: MemoryPool,
  private val handler: suspend BufferedServerNioDatagram.() -> Unit,
) : AsyncDatagramServer(port, { handler(BufferedServerNioDatagram(memoryPool, this)) })