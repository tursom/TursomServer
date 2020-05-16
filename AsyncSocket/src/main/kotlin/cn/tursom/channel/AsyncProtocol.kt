package cn.tursom.channel

import cn.tursom.core.timer.TimerTask
import cn.tursom.niothread.NioProtocol
import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object AsyncProtocol: NioProtocol {
  data class Context(val cont: Continuation<Int>, val timeoutTask: TimerTask? = null)
  data class ConnectContext(val cont: Continuation<SelectionKey>, val timeoutTask: TimerTask? = null)

  override fun handleConnect(key: SelectionKey, nioThread: NioThread) {
    key.interestOps(0)
    val context = key.attachment() as ConnectContext? ?: return
    context.timeoutTask?.cancel()
    context.cont.resume(key)
  }

  override fun handleRead(key: SelectionKey, nioThread: NioThread) {
    key.interestOps(0)
    //logE("read ready")
    val context = key.attachment() as Context? ?: return
    context.timeoutTask?.cancel()
    context.cont.resume(0)
  }

  override fun handleWrite(key: SelectionKey, nioThread: NioThread) {
    key.interestOps(0)
    val context = key.attachment() as Context? ?: return
    context.timeoutTask?.cancel()
    context.cont.resume(0)
  }

  override fun exceptionCause(key: SelectionKey, nioThread: NioThread, e: Throwable) {
    key.interestOps(0)
    val context = key.attachment() as Context?
    if (context != null)
      context.cont.resumeWithException(e)
    else {
      key.cancel()
      key.channel().close()
      e.printStackTrace()
    }
  }
}