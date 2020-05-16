package cn.tursom.socket

import cn.tursom.channel.AsyncChannel
import cn.tursom.channel.AsyncProtocol
import cn.tursom.niothread.WorkerLoopNioThread
import cn.tursom.niothread.loophandler.WorkerLoopHandler
import java.net.InetSocketAddress
import java.net.SocketException
import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("MemberVisibilityCanBePrivate")
object NioClient {
  @JvmStatic
  private val nioThread = WorkerLoopNioThread(
    "nioClient",
    daemon = false,
    workLoop = WorkerLoopHandler(AsyncProtocol)
  )

  suspend fun connect(host: String, port: Int, timeout: Long = 0): NioSocket {
    val key = getConnection(host, port, timeout)
    return NioSocket(key, nioThread)
  }

  private suspend fun getConnection(host: String, port: Int, timeout: Long): SelectionKey {
    return suspendCoroutine { cont ->
      val channel = SocketChannel.open()!!
      channel.configureBlocking(false)
      nioThread.register(channel, SelectionKey.OP_CONNECT) { key ->
        key.attach(AsyncProtocol.ConnectContext(cont, if (timeout > 0) AsyncChannel.timer.exec(timeout) {
          channel.close()
          cont.resumeWithException(TimeoutException())
        } else {
          null
        }))
      }
      channel.connect(InetSocketAddress(host, port))
    }
  }
}