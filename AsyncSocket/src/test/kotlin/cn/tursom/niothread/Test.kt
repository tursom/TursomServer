package cn.tursom.niothread

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.niothread.loophandler.BossLoopHandler
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

/**
 * 一个Echo服务器实现
 */
fun main() {
  val port = 12345
  val memoryPool = DirectMemoryPool()
  val protocol = object : NioProtocol {
    override fun handleConnect(key: SelectionKey, nioThread: NioThread) {
      key.interestOps(SelectionKey.OP_READ)
    }

    override fun handleRead(key: SelectionKey, nioThread: NioThread) {
      val buffer = memoryPool.get()
      (key.channel() as SocketChannel).read(buffer)
      key.interestOps(SelectionKey.OP_WRITE)
    }

    override fun handleWrite(key: SelectionKey, nioThread: NioThread) {
      (key.attachment() as ByteBuffer).use { buffer ->
        (key.channel() as SocketChannel).write(buffer)
      }
      key.interestOps(SelectionKey.OP_READ)
    }
  }
  val handler = BossLoopHandler(protocol)
  val nioThread = WorkerLoopNioThread(workLoop = handler, daemon = false)
  val serverChannel = ServerSocketChannel.open()
  serverChannel.socket().bind(InetSocketAddress(port))
  serverChannel.configureBlocking(false)
  nioThread.register(serverChannel, SelectionKey.OP_ACCEPT)
}