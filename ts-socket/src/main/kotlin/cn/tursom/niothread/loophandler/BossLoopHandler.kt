package cn.tursom.niothread.loophandler

import cn.tursom.niothread.NioProtocol
import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel

open class BossLoopHandler(
  private val protocol: NioProtocol,
  private val workerThread: NioThread? = null
) : (NioThread, SelectionKey) -> Unit {
  override fun invoke(nioThread: NioThread, key: SelectionKey) {
    val workerThread: NioThread = workerThread ?: nioThread
    handle(nioThread, key, workerThread)
  }

  fun handle(nioThread: NioThread, key: SelectionKey, workerThread: NioThread) {
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
      protocol.exceptionCause(key, nioThread, e)
    }
  }
}