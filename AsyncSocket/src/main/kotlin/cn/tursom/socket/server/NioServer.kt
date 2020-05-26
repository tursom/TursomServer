package cn.tursom.socket.server

import cn.tursom.channel.AsyncProtocol
import cn.tursom.niothread.NioThread
import cn.tursom.socket.AsyncSocket
import cn.tursom.niothread.NioProtocol
import cn.tursom.socket.NioSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.channels.SelectionKey

/**
 * 只有一个管理线程的协程套接字服务器
 * 协程运行的线程是独立于管理线程的
 */
open class NioServer(
  override val port: Int,
  backlog: Int = 50,
  coroutineScope: CoroutineScope = GlobalScope,
  var autoCloseSocket: Boolean = true,
  private val handler: suspend AsyncSocket.() -> Unit
) : SocketServer by NioLoopServer(port, object : NioProtocol by AsyncProtocol {
  override fun handleConnect(key: SelectionKey, nioThread: NioThread) {
    coroutineScope.launch {
      val socket = NioSocket(key, nioThread)
      try {
        socket.handler()
      } finally {
        if (autoCloseSocket) try {
          socket.close()
        } catch (e: Exception) {
        }
      }
    }
  }
}, backlog)

