package cn.tursom.datagram.server

import cn.tursom.niothread.NioProtocol
import cn.tursom.niothread.NioThread
import cn.tursom.niothread.WorkerLoopNioThread
import cn.tursom.niothread.loophandler.BossLoopHandler
import cn.tursom.niothread.loophandler.WorkerLoopHandler
import cn.tursom.socket.server.SocketServer
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.util.concurrent.atomic.AtomicBoolean

open class LoopDatagramServer(
  override val port: Int,
  protocol: NioProtocol,
  nioThreadFactory: (
    threadName: String,
    workLoop: (thread: NioThread, key: SelectionKey) -> Unit
  ) -> NioThread = { name, workLoop ->
    WorkerLoopNioThread(name, workLoop = workLoop, daemon = false)
  }
) : SocketServer {
  protected val listenChannel: DatagramChannel = DatagramChannel.open()
  val bossNioThread = nioThreadFactory("nio-boss", WorkerLoopHandler(protocol))
  private val started = AtomicBoolean(false)
  protected lateinit var key: SelectionKey

  override fun run() {
    if (started.compareAndSet(false, true)) {
      listenChannel.bind(InetSocketAddress(port))
      listenChannel.configureBlocking(false)
      bossNioThread.register(listenChannel, SelectionKey.OP_READ) {
        it.attach(this)
        key = it
      }
    }
  }

  override fun close() {
    listenChannel.close()
    bossNioThread.close()
  }

  protected fun finalize() {
    close()
  }
}