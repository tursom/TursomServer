package cn.tursom.socket

import cn.tursom.core.logE
import cn.tursom.socket.niothread.WorkerLoopNioThread
import java.net.InetSocketAddress
import java.net.SocketException
import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("MemberVisibilityCanBePrivate")
object AsyncNioClient {
  private const val TIMEOUT = 1000L
  private val protocol = AsyncNioSocket.nioSocketProtocol
  @JvmStatic
  private val nioThread = WorkerLoopNioThread("nioClient", isDaemon = true) { nioThread ->
    val selector = nioThread.selector
    //logE("client keys: ${selector.keys().size}")
    //logE("client op read: ${selector.keys().filter { key ->
    //  key.isValid && key.interestOps() == SelectionKey.OP_READ
    //}.size}")
    //logE("client op write: ${selector.keys().filter { key ->
    //  key.isValid && key.interestOps() == SelectionKey.OP_WRITE
    //}.size}")
    //logE("AsyncNioClient selector select")
    if (selector.select(TIMEOUT) != 0) {
      //logE("AsyncNioClient selector select successfully")
      val keyIter = selector.selectedKeys().iterator()
      while (keyIter.hasNext()) {
        val key = keyIter.next()
        keyIter.remove()
        try {
          when {
            //!key.isValid -> {
            //}
            //key.isConnectable -> {
            //  protocol.handleConnect(key, nioThread)
            //}
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
    //logE("AsyncNioClient selector select end")
  }

  suspend fun connect(host: String, port: Int): AsyncNioSocket {
    return connect(host, port, 0)
  }

  suspend fun connect(host: String, port: Int, timeout: Long): AsyncNioSocket {
    val key: SelectionKey = suspendCoroutine { cont ->
      val channel = getConnection(host, port)
      val timeoutTask = if (timeout > 0) AsyncNioSocket.timer.exec(timeout) {
        channel.close()
        cont.resumeWithException(TimeoutException())
      } else {
        null
      }
      nioThread.register(channel, 0) { key ->
        //key.attach(AsyncNioSocket.ConnectContext(cont, timeoutTask))
        timeoutTask?.cancel()
        cont.resume(key)
      }
    }
    return AsyncNioSocket(key, nioThread)
  }

  private fun getConnection(host: String, port: Int): SelectableChannel {
    val channel = SocketChannel.open()!!
    if (!channel.connect(InetSocketAddress(host, port))) {
      throw SocketException("connection failed")
    }
    channel.configureBlocking(false)
    return channel
  }
}