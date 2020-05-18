package cn.tursom.datagram.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap

class AsyncDatagramServer(
  port: Int,
  private val handler: suspend ServerNioDatagram.() -> Unit
) : LoopDatagramServer(port, DatagramProtocol) {
  private val channelMap = ConcurrentHashMap<SocketAddress, ServerNioDatagram>()

  private fun initChannel(address: SocketAddress): ServerNioDatagram {
    val datagram = ServerNioDatagram(address, listenChannel, key, bossNioThread)
    GlobalScope.launch { handler(datagram) }
    return datagram
  }

  fun getChannel(address: SocketAddress): ServerNioDatagram {
    var channel = channelMap[address]
    if (channel != null) {
      return channel
    }
    channel = initChannel(address)
    channelMap[address] = channel
    return channel
  }
}

