package cn.tursom.socket

import cn.tursom.channel.AsyncChannel.Companion.emptyBufferLongCode
import cn.tursom.channel.write
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

/**
 * 异步协程套接字对象
 */
class NioSocket internal constructor(
  override val key: SelectionKey,
  override val nioThread: NioThread,
) : AsyncSocket {
  companion object {
    suspend operator fun invoke(
      host: String,
      port: Int,
      timeout: Long = 0,
    ) = NioClient.connect(host, port, timeout)
  }

  override val channel: SocketChannel = key.channel() as SocketChannel
  override val open: Boolean get() = channel.isOpen && key.isValid

  override suspend fun read(buffer: ByteBuffer, timeout: Long): Long {
    if (buffer.writeable == 0) return emptyBufferLongCode
    return write(timeout) {
      channel.read(buffer)
    }
  }

  override suspend fun write(buffer: ByteBuffer, timeout: Long): Long {
    if (buffer.readable == 0) return emptyBufferLongCode
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

  protected fun finalize() {
    close()
  }

  override fun toString(): String {
    return "NioSocket(key=$key, nioThread=$nioThread, channel=$channel)"
  }
}