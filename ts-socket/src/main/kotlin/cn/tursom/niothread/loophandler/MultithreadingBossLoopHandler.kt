package cn.tursom.niothread.loophandler

import cn.tursom.core.Utils
import cn.tursom.niothread.NioProtocol
import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey

class MultithreadingBossLoopHandler(
  protocol: NioProtocol,
  private val workerThread: List<NioThread> = emptyList(),
) : BossLoopHandler(protocol, null) {
  override fun invoke(nioThread: NioThread, key: SelectionKey) {
    val workerThread: NioThread = if (workerThread.isEmpty()) {
      nioThread
    } else {
      workerThread[Utils.random.nextInt(0, workerThread.size - 1)]
    }
    handle(nioThread, key, workerThread)
  }
}