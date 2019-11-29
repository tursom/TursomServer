package cn.tursom.socket

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.timer.Timer
import cn.tursom.core.timer.TimerTask
import cn.tursom.core.timer.WheelTimer
import cn.tursom.niothread.NioThread
import java.net.SocketException
import java.nio.channels.FileChannel
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 异步协程套接字对象
 */
class NioSocket(override val key: SelectionKey, override val nioThread: NioThread) : AsyncSocket {
  override val channel: SocketChannel = key.channel() as SocketChannel
  override val open: Boolean get() = channel.isOpen && key.isValid

  override suspend fun <T> write(timeout: Long, action: () -> T): T {
    return operate {
      waitWrite(timeout)
      action()
    }
  }

  override suspend fun <T> read(timeout: Long, action: () -> T): T {
    return operate {
      waitRead(timeout)
      action()
    }
  }

  override suspend fun read(buffer: ByteBuffer, timeout: Long): Int {
    if (buffer.writeable == 0) return emptyBufferCode
    return write(timeout) {
      channel.read(buffer)
    }
  }

  override suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long): Long {
    if (buffer.isEmpty() && buffer.all { it.writeable != 0 }) return emptyBufferLongCode
    return read(timeout) {
      channel.read(buffer)
    }
  }

  override suspend fun write(buffer: ByteBuffer, timeout: Long): Int {
    if (buffer.readable == 0) return emptyBufferCode
    return write(timeout) {
      channel.write(buffer)
    }
  }

  override suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long): Long {
    if (buffer.isEmpty() && buffer.all { it.readable != 0 }) return emptyBufferLongCode
    return write(timeout) {
      channel.write(buffer)
    }
  }

  override fun close() {
    if (channel.isOpen || key.isValid) {
      nioThread.execute {
        channel.close()
        key.cancel()
      }
      nioThread.wakeup()
    }
  }

  private inline fun <T> operate(action: () -> T): T {
    return try {
      action()
    } catch (e: Exception) {
      waitMode()
      throw e
    }
  }

  private suspend inline fun waitRead(timeout: Long = 0) {
    suspendCoroutine<Int> {
      key.attach(Context(it, if (timeout > 0) timer.exec(timeout) {
        key.attach(null)
        waitMode()
        it.resumeWithException(TimeoutException())
      } else null))
      readMode()
      nioThread.wakeup()
    }
  }

  private suspend inline fun waitWrite(timeout: Long = 0) {
    suspendCoroutine<Int> {
      key.attach(Context(it, if (timeout > 0) timer.exec(timeout) {
        key.attach(null)
        waitMode()
        it.resumeWithException(TimeoutException())
      } else null))
      writeMode()
      nioThread.wakeup()
    }
  }

  data class Context(val cont: Continuation<Int>, val timeoutTask: TimerTask? = null)
  data class ConnectContext(val cont: Continuation<SelectionKey>, val timeoutTask: TimerTask? = null)

  protected fun finalize() {
    close()
  }

  /**
   * 伴生对象
   */
  companion object {

    val nioSocketProtocol = object : NioProtocol {
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

    //val timer = StaticWheelTimer.timer
    val timer: Timer = WheelTimer.timer

    const val emptyBufferCode = 0
    const val emptyBufferLongCode = 0L
  }
}