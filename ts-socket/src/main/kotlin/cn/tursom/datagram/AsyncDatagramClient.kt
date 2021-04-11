package cn.tursom.datagram

import cn.tursom.channel.AsyncProtocol
import cn.tursom.niothread.WorkerLoopNioThread
import cn.tursom.niothread.loophandler.WorkerLoopHandler
import cn.tursom.niothread.registerSuspend
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey

object AsyncDatagramClient {
  @JvmStatic
  private val nioThread = WorkerLoopNioThread(
    "nioClient",
    daemon = false,
    workLoop = WorkerLoopHandler(AsyncProtocol)
  )

  suspend fun connect(host: String, port: Int): NioDatagram = connect(InetSocketAddress(host, port))

  suspend fun connect(address: SocketAddress): NioDatagram {
    val channel = getConnection(address)
    val key: SelectionKey = nioThread.registerSuspend(channel, 0)
    return NioDatagram(channel, key, nioThread)
  }

  private fun getConnection(address: SocketAddress): DatagramChannel {
    val channel = DatagramChannel.open()!!
    channel.connect(address)
    channel.configureBlocking(false)
    return channel
  }
}