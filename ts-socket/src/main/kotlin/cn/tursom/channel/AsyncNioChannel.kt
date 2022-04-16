package cn.tursom.channel

import cn.tursom.channel.AsyncChannel.Companion.emptyBufferLongCode
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.timer.Timer
import cn.tursom.core.timer.WheelTimer
import cn.tursom.niothread.NioThread
import java.net.SocketException
import java.nio.channels.*
import java.nio.charset.Charset
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface AsyncNioChannel : AsyncChannel {
  val key: SelectionKey
  val nioThread: NioThread
  val channel: SelectableChannel get() = key.channel()
  override suspend fun write(buffer: ByteBuffer, timeout: Long): Long = write(arrayOf(buffer), timeout)
  override suspend fun read(buffer: ByteBuffer, timeout: Long): Long = read(arrayOf(buffer), timeout)

  override suspend fun write(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long,
  ): Long = write(timeout) {
    file.transferTo(position, count, channel as WritableByteChannel)
  }

  override suspend fun read(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long,
  ): Long = read(timeout) {
    file.transferFrom(channel as ReadableByteChannel, position, count)
  }

  override suspend fun write(bytes: ByteArray, offset: Int, len: Int): Long {
    return write(HeapByteBuffer(bytes, offset, len))
  }

  override suspend fun write(str: String, charset: Charset): Long {
    return write(str.toByteArray(charset))
  }

  override suspend fun read(pool: MemoryPool, timeout: Long): ByteBuffer = read(timeout) {
    val buffer = pool.get()
    if ((channel as ReadableByteChannel).read(buffer) < 0) throw SocketException()
    buffer
  }

  fun waitMode() {
    if (Thread.currentThread() == nioThread.thread) {
      if (key.isValid) key.interestOps(0)
    } else {
      nioThread.execute { if (key.isValid) key.interestOps(0) }
      nioThread.wakeup()
    }
  }

  fun readMode() {
    if (Thread.currentThread() == nioThread.thread) {
      if (key.isValid) key.interestOps(SelectionKey.OP_WRITE)
    } else {
      nioThread.execute {
        if (key.isValid) key.interestOps(SelectionKey.OP_READ)
      }
      nioThread.wakeup()
    }
  }

  fun writeMode() {
    if (Thread.currentThread() == nioThread.thread) {
      if (key.isValid) key.interestOps(SelectionKey.OP_WRITE)
    } else {
      nioThread.execute {
        if (key.isValid) {
          key.interestOps(SelectionKey.OP_WRITE)
        }
      }
      nioThread.wakeup()
    }
  }

  /**
   * 如果通道已断开则会抛出异常
   */
  override suspend fun recv(buffer: ByteBuffer, timeout: Long): Long {
    if (buffer.writeable == 0) return emptyBufferLongCode
    val readSize = read(buffer, timeout)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }

  override suspend fun recv(buffers: Array<out ByteBuffer>, timeout: Long): Long {
    if (buffers.isEmpty()) return emptyBufferLongCode
    val readSize = read(buffers, timeout)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }

  suspend fun waitRead(timeout: Long = 0) {
    suspendCoroutine<Int> {
      key.attach(AsyncProtocol.Context(it, if (timeout > 0) timer.exec(timeout) {
        key.attach(null)
        waitMode()
        it.resumeWithException(TimeoutException())
      } else null))
      readMode()
      nioThread.wakeup()
    }
  }

  suspend fun waitWrite(timeout: Long = 0) {
    suspendCoroutine<Int> {
      key.attach(AsyncProtocol.Context(it, if (timeout > 0) timer.exec(timeout) {
        key.attach(null)
        waitMode()
        it.resumeWithException(TimeoutException())
      } else null))
      writeMode()
      nioThread.wakeup()
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

  companion object {
    //val timer = StaticWheelTimer.timer
    val timer: Timer = WheelTimer.timer
  }
}

inline fun <T> AsyncNioChannel.operate(action: () -> T): T {
  return try {
    action()
  } catch (e: Exception) {
    waitMode()
    throw e
  }
}

suspend fun <T> AsyncNioChannel.write(timeout: Long, action: () -> T): T {
  return operate {
    waitWrite(timeout)
    action()
  }
}

suspend fun <T> AsyncNioChannel.read(timeout: Long, action: () -> T): T {
  return operate {
    waitRead(timeout)
    action()
  }
}