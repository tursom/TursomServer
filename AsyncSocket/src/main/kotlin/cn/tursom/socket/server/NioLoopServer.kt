package cn.tursom.socket.server

import cn.tursom.niothread.WorkerLoopNioThread
import cn.tursom.niothread.loophandler.BossLoopHandler
import cn.tursom.socket.NioProtocol
import cn.tursom.niothread.loophandler.WorkerLoopHandler
import cn.tursom.niothread.NioThread
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 工作在单线程上的 Nio 服务器。
 */
class NioLoopServer(
  override val port: Int,
  private val protocol: NioProtocol,
  private val backLog: Int = 50,
  nioThreadFactory: (
    threadName: String,
    workLoop: (thread: NioThread, key: SelectionKey) -> Unit
  ) -> NioThread = { name, workLoop ->
    WorkerLoopNioThread(name, workLoop = workLoop, daemon = false)
  }
) : SocketServer {
  private val listenChannel = ServerSocketChannel.open()
  private val workerNioThread = nioThreadFactory("nio-worker", WorkerLoopHandler(
    protocol
  )::handle)
  private val bossNioThread = nioThreadFactory("nio-boss", BossLoopHandler(
    protocol,
    workerNioThread
  )::handle)
  private val started = AtomicBoolean(false)

  override fun run() {
    if (started.compareAndSet(false, true)) {
      listenChannel.socket().bind(InetSocketAddress(port), backLog)
      listenChannel.configureBlocking(false)
      bossNioThread.register(listenChannel, SelectionKey.OP_ACCEPT) {}
    }
  }

  override fun close() {
    listenChannel.close()
    workerNioThread.close()
    bossNioThread.close()
  }

  protected fun finalize() {
    close()
  }

  companion object {
    private const val TIMEOUT = 1000L
  }
}