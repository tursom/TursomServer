package cn.tursom.channel

import cn.tursom.buffer.MultipleByteBuffer
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.timer.Timer
import cn.tursom.core.timer.WheelTimer
import cn.tursom.niothread.NioThread
import java.io.Closeable
import java.net.SocketAddress
import java.net.SocketException
import java.nio.channels.*
import java.nio.charset.Charset
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface AsyncChannel : Closeable {
  val open: Boolean
  val remoteAddress: SocketAddress
  fun getBuffed(pool: MemoryPool): BufferedAsyncChannel = BufferedAsyncChannelImpl(pool, this)

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
  ): Long

  suspend fun read(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long = 0
  ): Long

  suspend fun write(bytes: ByteArray, offset: Int = 0, len: Int = bytes.size - offset): Int {
    return write(HeapByteBuffer(bytes, offset, len))
  }

  suspend fun write(str: String, charset: Charset = Charsets.UTF_8): Int {
    return write(str.toByteArray(charset))
  }

  suspend fun read(pool: MemoryPool, timeout: Long = 0L): ByteBuffer

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