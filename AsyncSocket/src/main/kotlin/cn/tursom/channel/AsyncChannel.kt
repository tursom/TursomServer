package cn.tursom.channel

import cn.tursom.buffer.MultipleByteBuffer
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.timer.Timer
import cn.tursom.core.timer.WheelTimer
import cn.tursom.niothread.NioThread
import java.io.Closeable
import java.net.SocketException
import java.nio.channels.*
import java.nio.charset.Charset
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface AsyncChannel : Closeable {
  val open: Boolean
  val key: SelectionKey
  val nioThread: NioThread
  val channel: SelectableChannel get() = key.channel()
  fun getBuffed(pool: MemoryPool): BufferedAsyncChannel = BufferedAsyncChannelImpl(pool, this)

  private inline fun <T> operate(action: () -> T): T {
    return try {
      action()
    } catch (e: Exception) {
      waitMode()
      throw e
    }
  }

  suspend fun <T> write(timeout: Long, action: () -> T): T {
    return operate {
      waitWrite(timeout)
      action()
    }
  }

  suspend fun <T> read(timeout: Long, action: () -> T): T {
    return operate {
      waitRead(timeout)
      action()
    }
  }

  suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun write(buffer: ByteBuffer, timeout: Long = 0L): Int = write(arrayOf(buffer), timeout).toInt()
  suspend fun read(buffer: ByteBuffer, timeout: Long = 0L): Int = read(arrayOf(buffer), timeout).toInt()
  suspend fun write(buffer: MultipleByteBuffer, timeout: Long = 0L): Long = write(buffer.buffers, timeout)
  suspend fun read(buffer: MultipleByteBuffer, timeout: Long = 0L): Long = read(buffer.buffers, timeout)

  suspend fun write(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long = 0
  ): Long = write(timeout) {
    file.transferTo(position, count, channel as WritableByteChannel)
  }

  suspend fun read(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long = 0
  ): Long = read(timeout) {
    file.transferFrom(channel as ReadableByteChannel, position, count)
  }

  suspend fun write(bytes: ByteArray, offset: Int = 0, len: Int = bytes.size - offset): Int {
    return write(HeapByteBuffer(bytes, offset, len))
  }

  suspend fun write(str: String, charset: Charset = Charsets.UTF_8): Int {
    return write(str.toByteArray(charset))
  }

  suspend fun read(pool: MemoryPool, timeout: Long = 0L): ByteBuffer

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
  suspend fun recv(buffer: ByteBuffer, timeout: Long = 0): Int {
    if (buffer.writeable == 0) return emptyBufferCode
    val readSize = read(buffer, timeout)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }

  suspend fun recv(buffers: Array<out ByteBuffer>, timeout: Long = 0): Long {
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
    const val emptyBufferCode = 0
    const val emptyBufferLongCode = 0L

    //val timer = StaticWheelTimer.timer
    val timer: Timer = WheelTimer.timer
  }
}