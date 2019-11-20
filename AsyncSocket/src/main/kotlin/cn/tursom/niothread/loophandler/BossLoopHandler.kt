package cn.tursom.niothread.loophandler

import cn.tursom.niothread.NioThread
import cn.tursom.socket.NioProtocol
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel

class BossLoopHandler(private val protocol: NioProtocol, private val workerThread: NioThread? = null) {
  fun handle(nioThread: NioThread, key: SelectionKey) {
    val workerThread: NioThread = workerThread ?: nioThread
    try {
      when {
        key.isAcceptable -> {
          val serverChannel = key.channel() as ServerSocketChannel
          while (true) {
            val channel = serverChannel.accept() ?: break
            channel.configureBlocking(false)
            workerThread.register(channel, 0) {
              protocol.handleConnect(it, workerThread)
            }
          }
        }
      }
    } catch (e: Throwable) {
      try {
        protocol.exceptionCause(key, nioThread, e)
      } catch (e1: Throwable) {
        e.printStackTrace()
        e1.printStackTrace()
        key.cancel()
        key.channel().close()
      }
    }
  }
}