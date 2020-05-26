package cn.tursom.niothread.loophandler

import cn.tursom.core.randomInt
import cn.tursom.niothread.NioThread
import cn.tursom.niothread.NioProtocol
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel

class MultithreadingBossLoopHandler(
  protocol: NioProtocol,
  private val workerThread: List<NioThread> = emptyList()
) : BossLoopHandler(protocol, null) {
  override fun invoke(nioThread: NioThread, key: SelectionKey) {
    val workerThread: NioThread = if (workerThread.isEmpty()) {
      nioThread
    } else {
      workerThread[randomInt(0, workerThread.size - 1)]
    }
    handle(nioThread, key, workerThread)
  }
}