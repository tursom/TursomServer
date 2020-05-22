package cn.tursom.socket

import cn.tursom.channel.AsyncChannel.Companion.emptyBufferCode
import cn.tursom.channel.AsyncChannel.Companion.emptyBufferLongCode
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

/**
 * 异步协程套接字对象
 */
class NioSocket(override val key: SelectionKey, override val nioThread: NioThread) : AsyncSocket {
  override val channel: SocketChannel = key.channel() as SocketChannel
  override val open: Boolean get() = channel.isOpen && key.isValid

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

  protected fun finalize() {
    close()
  }

  override fun toString(): String {
    return "NioSocket(key=$key, nioThread=$nioThread, channel=$channel)"
  }
}