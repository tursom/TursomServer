package cn.tursom.datagram.server

import cn.tursom.core.pool.MemoryPool
import cn.tursom.datagram.AsyncDatagram
import cn.tursom.datagram.BufferedAsyncDatagram

class BufferedServerNioDatagram(override val pool: MemoryPool, override val prevChannel: ServerNioDatagram) :
  BufferedAsyncDatagram,
  AsyncDatagram by prevChannel {
  override fun toString(): String {
    return "BufferedServerNioDatagram(pool=$pool, prevChannel=$prevChannel)"
  }
}