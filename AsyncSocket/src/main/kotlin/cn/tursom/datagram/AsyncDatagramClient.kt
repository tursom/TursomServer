package cn.tursom.datagram

import cn.tursom.channel.AsyncProtocol
import cn.tursom.niothread.WorkerLoopNioThread
import cn.tursom.niothread.loophandler.WorkerLoopHandler
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AsyncDatagramClient {
  private const val TIMEOUT = 1000L

  @JvmStatic
  private val nioThread = WorkerLoopNioThread(
    "nioClient",
    daemon = false,
    workLoop = WorkerLoopHandler(AsyncProtocol)::handle
  )

  suspend fun connect(host: String, port: Int, timeout: Long = 0): NioDatagram {
    val channel = getConnection(host, port)
    val key: SelectionKey = suspendCoroutine { cont ->
      nioThread.register(channel, 0) { key ->
        cont.resume(key)
      }
    }
    return NioDatagram(channel, key, nioThread)
  }

  private fun getConnection(host: String, port: Int): DatagramChannel {
    val channel = DatagramChannel.open()!!
    channel.connect(InetSocketAddress(host, port))
    channel.configureBlocking(false)
    return channel
  }
}