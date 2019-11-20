package cn.tursom.socket

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
  private const val TIMEOUT = 1000L
  private val protocol = NioSocket.nioSocketProtocol
  @JvmStatic
  private val nioThread = WorkerLoopNioThread(
    "nioClient",
    daemon = true,
    workLoop = WorkerLoopHandler(protocol)::handle
  )

  suspend fun connect(host: String, port: Int, timeout: Long = 0): NioSocket {
    val key: SelectionKey = suspendCoroutine { cont ->
      val channel = getConnection(host, port)
      val timeoutTask = if (timeout > 0) NioSocket.timer.exec(timeout) {
        channel.close()
        cont.resumeWithException(TimeoutException())
      } else {
        null
      }
      nioThread.register(channel, 0) { key ->
        timeoutTask?.cancel()
        cont.resume(key)
      }
    }
    return NioSocket(key, nioThread)
  }

  private fun getConnection(host: String, port: Int): SelectableChannel {
    val channel = SocketChannel.open()!!
    if (!channel.connect(InetSocketAddress(host, port))) {
      throw SocketException("connection failed")
    }
    channel.configureBlocking(false)
    return channel
  }
}