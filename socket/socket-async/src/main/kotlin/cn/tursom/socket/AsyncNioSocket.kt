package cn.tursom.socket

import cn.tursom.socket.niothread.INioThread
import cn.tursom.core.timer.TimerTask
import cn.tursom.core.timer.WheelTimer
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 利用 SelectionKey 的 attachment 进行状态的传输
 * 导致该类无法利用 SelectionKey 的 attachment
 * 但是对于一般的应用而言是足够使用的
 */
class AsyncNioSocket(override val key: SelectionKey, override val nioThread: INioThread) : IAsyncNioSocket {
  override val channel: SocketChannel = key.channel() as SocketChannel

  private suspend inline fun <T> operate(crossinline action: (Continuation<T>) -> Unit): T {
    return try {
      suspendCoroutine {
        action(it)
      }
    } catch (e: Exception) {
      waitMode()
      throw RuntimeException(e)
    }
  }

  override suspend fun read(buffer: ByteBuffer): Int {
    if (buffer.remaining() == 0) return emptyBufferCode
    return operate {
      key.attach(SingleContext(buffer, it))
      readMode()
      nioThread.wakeup()
    }
  }

  override suspend fun read(buffer: Array<out ByteBuffer>): Long {
    if (buffer.isEmpty()) return emptyBufferLongCode
    return operate {
      key.attach(MultiContext(buffer, it))
      readMode()
      nioThread.wakeup()
    }
  }

  override suspend fun write(buffer: ByteBuffer): Int {
    if (buffer.remaining() == 0) return emptyBufferCode
    return operate {
      key.attach(SingleContext(buffer, it))
      writeMode()
      nioThread.wakeup()
    }
  }

  override suspend fun write(buffer: Array<out ByteBuffer>): Long {
    if (buffer.isEmpty()) return emptyBufferLongCode
    return operate {
      key.attach(MultiContext(buffer, it))
      writeMode()
      nioThread.wakeup()
    }
  }

  override suspend fun read(buffer: ByteBuffer, timeout: Long): Int {
    if (timeout <= 0) return read(buffer)
    if (buffer.remaining() == 0) return emptyBufferCode
    return operate {
      key.attach(
          SingleContext(
              buffer,
              it,
              timer.exec(timeout) {
                key.attach(null)
                it.resumeWithException(TimeoutException())
              })
      )
      readMode()
      nioThread.wakeup()
    }
  }

  override suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long): Long {
    if (timeout <= 0) return read(buffer)
    if (buffer.isEmpty()) return emptyBufferLongCode
    return operate {
      key.attach(
          MultiContext(
              buffer,
              it,
              timer.exec(timeout) {
                key.attach(null)
                it.resumeWithException(TimeoutException())
              })
      )
      readMode()
      nioThread.wakeup()
    }
  }

  override suspend fun write(buffer: ByteBuffer, timeout: Long): Int {
    if (timeout <= 0) return write(buffer)
    if (buffer.remaining() == 0) return emptyBufferCode
    return operate {
      key.attach(
          SingleContext(
              buffer,
              it,
              timer.exec(timeout) {
                key.attach(null)
                it.resumeWithException(TimeoutException())
              })
      )
      writeMode()
      nioThread.wakeup()
    }
  }

  override suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long): Long {
    if (timeout <= 0) return write(buffer)
    if (buffer.isEmpty()) return emptyBufferLongCode
    return operate {
      key.attach(
          MultiContext(
              buffer,
              it,
              timer.exec(timeout) {
                key.attach(null)
                it.resumeWithException(TimeoutException())
              })
      )
      writeMode()
      nioThread.wakeup()
    }
  }

  override fun close() {
    nioThread.execute {
      channel.close()
      key.cancel()
    }
  }

  interface Context {
    val cont: Continuation<*>
    val timeoutTask: TimerTask? get() = null
  }

  class SingleContext(
      val buffer: ByteBuffer,
      override val cont: Continuation<Int>,
      override val timeoutTask: TimerTask? = null
  ) : Context

  class MultiContext(
      val buffer: Array<out ByteBuffer>,
      override val cont: Continuation<Long>,
      override val timeoutTask: TimerTask? = null
  ) : Context

  companion object {
    val nioSocketProtocol = object : INioProtocol {
      override fun handleConnect(key: SelectionKey, nioThread: INioThread) {}

      override fun handleRead(key: SelectionKey, nioThread: INioThread) {
        key.interestOps(0)
        val context = key.attachment() as Context? ?: return
        context.timeoutTask?.cancel()
        if (context is SingleContext) {
          val channel = key.channel() as SocketChannel
          val readSize = channel.read(context.buffer)
          context.cont.resume(readSize)
        } else {
          context as MultiContext
          val channel = key.channel() as SocketChannel
          val readSize = channel.read(context.buffer)
          context.cont.resume(readSize)
        }
      }

      override fun handleWrite(key: SelectionKey, nioThread: INioThread) {
        key.interestOps(0)
        val context = key.attachment() as Context? ?: return
        context.timeoutTask?.cancel()
        if (context is SingleContext) {
          val channel = key.channel() as SocketChannel
          val readSize = channel.write(context.buffer)
          context.cont.resume(readSize)
        } else {
          context as MultiContext
          val channel = key.channel() as SocketChannel
          val readSize = channel.write(context.buffer)
          context.cont.resume(readSize)
        }
      }

      override fun exceptionCause(key: SelectionKey, nioThread: INioThread, e: Throwable) {
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
    val timer = WheelTimer.timer

    const val emptyBufferCode = 0
    const val emptyBufferLongCode = 0L
  }
}