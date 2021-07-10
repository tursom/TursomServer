package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelWriter

class StringObjectWriter(
  private val prevWriter: ChannelWriter<String>,
  private val toString: (obj: Any) -> String = { it.toString() }
) : ChannelWriter<Any> {
  override suspend fun write(value: Any) = prevWriter.write(toString(value))
  override suspend fun flush(timeout: Long) = prevWriter.flush(timeout)
  override suspend fun writeAndFlush(value: Any, timeout: Long): Long =
    prevWriter.writeAndFlush(toString(value), timeout)

  override fun close() = prevWriter.close()
}