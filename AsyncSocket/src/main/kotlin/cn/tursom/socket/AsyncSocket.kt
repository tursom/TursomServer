package cn.tursom.socket

import cn.tursom.buffer.MultipleByteBuffer
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.MemoryPool
import cn.tursom.niothread.NioThread
import java.io.Closeable
import java.net.SocketException
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

interface AsyncSocket : Closeable {
  val open: Boolean
  val channel: SocketChannel
  val key: SelectionKey
  val nioThread: NioThread

  suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun write(buffer: ByteBuffer, timeout: Long = 0L): Int = write(arrayOf(buffer), timeout).toInt()
  suspend fun read(buffer: ByteBuffer, timeout: Long = 0L): Int = read(arrayOf(buffer), timeout).toInt()
  suspend fun write(buffer: MultipleByteBuffer, timeout: Long = 0L): Long = write(buffer.buffers, timeout)
  suspend fun read(buffer: MultipleByteBuffer, timeout: Long = 0L): Long = read(buffer.buffers, timeout)

  /**
   * 在有数据读取的时候自动由内存池分配内存
   */
  @Throws(SocketException::class)
  suspend fun read(pool: MemoryPool, timeout: Long = 0L): ByteBuffer

  override fun close()

  fun waitMode() {
    if (Thread.currentThread() == nioThread.thread) {
      if (key.isValid) key.interestOps(SelectionKey.OP_WRITE)
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
      nioThread.execute { if (key.isValid) key.interestOps(SelectionKey.OP_WRITE) }
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

  companion object {
    const val emptyBufferCode = 0
    const val emptyBufferLongCode = 0L
  }
}