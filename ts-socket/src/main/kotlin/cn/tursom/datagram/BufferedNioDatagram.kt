package cn.tursom.datagram

import cn.tursom.core.pool.MemoryPool

class BufferedNioDatagram(override val pool: MemoryPool, override val prevChannel: AsyncDatagram) :
  BufferedAsyncDatagram,
  AsyncDatagram by prevChannel