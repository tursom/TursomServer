package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer

class StringWriter(
  private val prevWriter: ChannelWriter<ByteBuffer>
) : ChannelWriter<String> {
  private val stringList = ArrayList<String>()
  override suspend fun write(value: String) {
    stringList.add(value)
  }

  override suspend fun flush(timeout: Long): Long {
    stringList.forEach { prevWriter.write(HeapByteBuffer(it.toByteArray())) }
    stringList.clear()
    return prevWriter.flush(timeout)
  }

  override fun close() {
    prevWriter.close()
  }

  override suspend fun writeAndFlush(value: String, timeout: Long): Long = if (stringList.isEmpty()) {
    prevWriter.writeAndFlush(HeapByteBuffer(value.toByteArray()), timeout)
  } else {
    super.writeAndFlush(value, timeout)
  }
}