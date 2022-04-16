package cn.tursom.channel.enhance.impl

import cn.tursom.channel.AsyncChannel
import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.core.buffer.ByteBuffer

class ChannelWriterImpl(
  private val socket: AsyncChannel,
) : ChannelWriter<ByteBuffer> {
  private val bufList = ArrayList<ByteBuffer>(4)
  override suspend fun write(value: ByteBuffer) {
    bufList.add(value)
  }

  override suspend fun flush(timeout: Long): Long {
    val read = when (bufList.size) {
      0 -> 0
      1 -> socket.write(bufList[0], timeout)
      else -> socket.write(bufList.toTypedArray(), timeout)
    }
    bufList.clear()
    return read
  }

  override suspend fun writeAndFlush(value: ByteBuffer, timeout: Long): Long {
    return if (bufList.isEmpty()) {
      socket.write(value, timeout)
    } else {
      super.writeAndFlush(value, timeout)
    }
  }

  override fun close() {
    socket.close()
  }
}