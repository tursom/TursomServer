package cn.tursom.datagram.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap

open class AsyncDatagramServer(
  port: Int,
  private val handler: suspend ServerNioDatagram.() -> Unit
) : LoopDatagramServer(port, DatagramProtocol) {
  private val channelMap = ConcurrentHashMap<SocketAddress, SoftReference<ServerNioDatagram>>()

  private fun initChannel(address: SocketAddress): ServerNioDatagram {
    val datagram = ServerNioDatagram(address, this, listenChannel, key, bossNioThread)
    GlobalScope.launch {
      datagram.use { datagram ->
        handler(datagram)
      }
    }
    return datagram
  }

  fun closeChannel(address: SocketAddress): ServerNioDatagram? {
    return channelMap.remove(address)?.get()
  }

  fun getChannel(address: SocketAddress): ServerNioDatagram {
    var channel = channelMap[address]?.get()
    if (channel != null) {
      return channel
    }
    channel = initChannel(address)
    channelMap[address] = SoftReference(channel)
    return channel
  }
}
