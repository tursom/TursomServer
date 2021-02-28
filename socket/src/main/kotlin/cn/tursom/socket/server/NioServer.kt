package cn.tursom.socket.server

import cn.tursom.socket.INioProtocol
import cn.tursom.socket.niothread.INioThread
import cn.tursom.socket.niothread.WorkerLoopNioThread
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 工作在单线程上的 Nio 服务器。
 */
class NioServer(
    override val port: Int,
    private val protocol: INioProtocol,
    backLog: Int = 50,
    val nioThreadGenerator: (threadName: String, workLoop: (thread: INioThread) -> Unit) -> INioThread
) : ISocketServer {
  private val listenChannel = ServerSocketChannel.open()
  private val threadList = ConcurrentLinkedDeque<INioThread>()
  
  init {
    listenChannel.socket().bind(InetSocketAddress(port), backLog)
    listenChannel.configureBlocking(false)
  }
  
  constructor(
      port: Int,
      protocol: INioProtocol,
      backLog: Int = 50
  ) : this(port, protocol, backLog, { name, workLoop ->
    WorkerLoopNioThread(name, workLoop = workLoop, isDaemon = false)
  })
  
  override fun run() {
    val nioThread = nioThreadGenerator("nio worker", LoopHandler(protocol)::handle)
    nioThread.register(listenChannel, SelectionKey.OP_ACCEPT) {}
    threadList.add(nioThread)
  }
  
  override fun close() {
    listenChannel.close()
    threadList.forEach {
      it.close()
    }
  }
  
  class LoopHandler(val protocol: INioProtocol) {
    fun handle(nioThread: INioThread) {
      //logE("wake up")
      val selector = nioThread.selector
      if (selector.isOpen) {
        if (selector.select(TIMEOUT) != 0) {
          val keyIter = selector.selectedKeys().iterator()
          while (keyIter.hasNext()) run whileBlock@{
            val key = keyIter.next()
            keyIter.remove()
            //logE("selected key: $key: ${key.attachment()}")
            try {
              when {
                key.isAcceptable -> {
                  val serverChannel = key.channel() as ServerSocketChannel
                  var channel = serverChannel.accept()
                  while (channel != null) {
                    channel.configureBlocking(false)
                    nioThread.register(channel, 0) {
                      protocol.handleConnect(it, nioThread)
                    }
                    channel = serverChannel.accept()
                  }
                }
                key.isReadable -> {
                  protocol.handleRead(key, nioThread)
                }
                key.isWritable -> {
                  protocol.handleWrite(key, nioThread)
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
      }
    }
  }
  
  companion object {
    private const val TIMEOUT = 1000L
  }
}