package cn.tursom.datagram.server

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.timer.TimerTask
import cn.tursom.core.timer.WheelTimer
import cn.tursom.datagram.NioDatagram
import cn.tursom.niothread.NioThread
import java.net.SocketAddress
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ServerNioDatagram(
  override val remoteAddress: SocketAddress,
  val server: AsyncDatagramServer,
  channel: DatagramChannel,
  key: SelectionKey,
  nioThread: NioThread
) : NioDatagram(channel, key, nioThread) {
  companion object {
    val timer = WheelTimer.timer
  }

  private val bufferList = ConcurrentLinkedQueue<ByteBuffer>()
  private var readBuffer: ByteBuffer? = null
  var cont: Continuation<Int>? = null
  private var timeoutTask: TimerTask? = null

  fun addBuffer(buffer: ByteBuffer) {
    bufferList.add(buffer)
  }

  override suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long): Long {
    var write = 0L
    buffer.forEach { buf ->
      write += buf.read { channel.send(it, remoteAddress) }
    }
    return write
  }

  override suspend fun waitRead(timeout: Long) {
    suspendCoroutine<Int> { cont ->
      this.cont = cont
      if (timeout > 0) {
        timeoutTask = timer.exec(timeout) {
          cont.resumeWithException(TimeoutException())
        }
      }
    }
    cont = null
    timeoutTask?.cancel()
  }

  override suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long): Long {
    if (bufferList.isEmpty()) waitRead()
    val bufferIterator = buffer.iterator()
    var write = 0L
    while (bufferIterator.hasNext()) {
      val buf = bufferIterator.next()
      while (buf.writeable != 0) {
        if (readBuffer == null || readBuffer?.readable == 0) {
          readBuffer = bufferList.poll() ?: return write
        }
        write += buf.put(readBuffer!!)
      }
    }
    return write
  }

  override suspend fun read(pool: MemoryPool, timeout: Long): ByteBuffer {
    if (bufferList.isEmpty()) waitRead()
    val buf = pool.get()
    read(buf, timeout)
    return buf
  }

  override fun close() {
    server.closeChannel(remoteAddress)
  }

  override fun toString(): String {
    return "ServerNioDatagram(remoteAddress=$remoteAddress, localAddress=${channel.localAddress})"
  }
}