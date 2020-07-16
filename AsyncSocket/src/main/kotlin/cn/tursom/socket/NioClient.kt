package cn.tursom.socket

import cn.tursom.channel.AsyncNioChannel
import cn.tursom.channel.AsyncProtocol
import cn.tursom.niothread.WorkerLoopNioThread
import cn.tursom.niothread.loophandler.WorkerLoopHandler
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("MemberVisibilityCanBePrivate")
object NioClient {
  @JvmStatic
  private val nioThread = WorkerLoopNioThread(
    "nioClient",
    daemon = true,
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
      channel.connect(InetSocketAddress(host, port))
      nioThread.register(channel, SelectionKey.OP_CONNECT) { key ->
        key.attach(AsyncProtocol.ConnectContext(cont, if (timeout > 0) AsyncNioChannel.timer.exec(timeout) {
          channel.close()
          cont.resumeWithException(TimeoutException())
        } else {
          null
        }))
      }
    }
  }
}