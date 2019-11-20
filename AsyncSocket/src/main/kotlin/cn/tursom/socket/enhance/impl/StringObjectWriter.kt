package cn.tursom.socket.enhance.impl

import cn.tursom.socket.enhance.SocketWriter

class StringObjectWriter(
  private val prevWriter: SocketWriter<String>,
  private val toString: (obj: Any) -> String = { it.toString() }
) : SocketWriter<Any> {
  override suspend fun write(value: Any) = prevWriter.write(toString(value))
  override suspend fun flush(timeout: Long) = prevWriter.flush(timeout)
  override fun close() = prevWriter.close()
}