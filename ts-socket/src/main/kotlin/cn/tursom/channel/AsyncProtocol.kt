package cn.tursom.channel

import cn.tursom.core.timer.TimerTask
import cn.tursom.core.util.assert
import cn.tursom.niothread.NioProtocol
import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object AsyncProtocol : NioProtocol {
  data class Context(val cont: Continuation<Int>, val timeoutTask: TimerTask? = null)
  data class ConnectContext(val cont: Continuation<SelectionKey>, val timeoutTask: TimerTask? = null)

  override fun handleConnect(key: SelectionKey, nioThread: NioThread) {
    key.interestOps(0)
    key.channel().assert<SocketChannel> { finishConnect() }
    key.attachment().assert<ConnectContext> {
      timeoutTask?.cancel()
      cont.resume(key)
    }
  }

  override fun handleRead(key: SelectionKey, nioThread: NioThread) {
    key.interestOps(0)
    key.attachment().assert<Context> {
      timeoutTask?.cancel()
      cont.resume(0)
    }
  }

  override fun handleWrite(key: SelectionKey, nioThread: NioThread) {
    key.interestOps(0)
    key.attachment().assert<Context> {
      timeoutTask?.cancel()
      cont.resume(0)
    }
  }

  override fun exceptionCause(key: SelectionKey, nioThread: NioThread, e: Throwable) {
    key.interestOps(0)
    if (!key.attachment().assert<Context> { cont.resumeWithException(e) }) {
      key.cancel()
      key.channel().close()
      e.printStackTrace()
    }
  }
}