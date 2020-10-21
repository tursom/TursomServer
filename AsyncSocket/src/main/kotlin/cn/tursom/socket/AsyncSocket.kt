package cn.tursom.socket

import cn.tursom.channel.AsyncNioChannel
import cn.tursom.channel.BufferedAsyncChannel
import cn.tursom.channel.read
import cn.tursom.channel.write
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import cn.tursom.core.pool.MemoryPool
import java.net.SocketAddress
import java.net.SocketException
import java.nio.channels.FileChannel
import java.nio.channels.SocketChannel

@Suppress("unused")
interface AsyncSocket : AsyncNioChannel {
  override val channel: SocketChannel
  override val remoteAddress: SocketAddress get() = channel.remoteAddress
  override fun getBuffed(pool: MemoryPool): BufferedAsyncChannel = BufferedNioSocket(this, pool)

  override suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long): Long =
    write(timeout) { channel.write(buffer) }

  override suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long): Long =
    read(timeout) { channel.read(buffer) }

  override suspend fun write(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long
  ): Long = write(timeout) {
    file.transferTo(position, count, channel)
  }

  override suspend fun read(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long
  ): Long = read(timeout) {
    file.transferFrom(channel, position, count)
  }

  /**
   * 在有数据读取的时候自动由内存池分配内存
   */
  override suspend fun read(pool: MemoryPool, timeout: Long): ByteBuffer = read(timeout) {
    val buffer = pool.get()
    if (channel.read(buffer) < 0) throw SocketException("socket closed")
    buffer
  }
}