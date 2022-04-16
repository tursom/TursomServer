package cn.tursom.channel

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.ArrayByteBuffer
import cn.tursom.core.pool.MemoryPool
import java.net.SocketException
import java.nio.channels.FileChannel

interface ReadableAsyncChannel {
  suspend fun read(buffer: ByteBuffer, timeout: Long = 0L): Long
  suspend fun read(
    buffer: Array<out ByteBuffer>,
    timeout: Long = 0L,
  ): Long = read(buffer, 0, buffer.size, timeout)

  suspend fun read(
    buffer: Array<out ByteBuffer>,
    offset: Int,
    length: Int,
    timeout: Long = 0L,
  ): Long = read(ArrayByteBuffer(buffer, offset, length), timeout)

  suspend fun read(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long = 0,
  ): Long

  suspend fun read(pool: MemoryPool, timeout: Long = 0L): ByteBuffer

  /**
   * 如果通道已断开则会抛出异常
   */
  suspend fun recv(buffer: ByteBuffer, timeout: Long = 0): Long {
    if (buffer.writeable == 0) return AsyncChannel.emptyBufferLongCode
    val readSize = read(buffer, timeout)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }

  suspend fun recv(buffers: Array<out ByteBuffer>, timeout: Long = 0): Long {
    if (buffers.isEmpty()) return AsyncChannel.emptyBufferLongCode
    val readSize = read(buffers, timeout)
    if (readSize < 0) {
      throw SocketException("channel closed")
    }
    return readSize
  }
}
