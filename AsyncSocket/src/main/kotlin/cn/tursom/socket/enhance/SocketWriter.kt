package cn.tursom.socket.enhance

import java.io.Closeable

interface SocketWriter<T> : Closeable {
  suspend fun write(value: T)
  suspend fun flush(timeout: Long = 0): Long
  override fun close()

  suspend fun write(vararg value: T) {
    value.forEach { write(it) }
  }

  suspend fun write(value: Collection<T>) {
    value.forEach { write(it) }
  }

  suspend fun writeAndFlush(value: T, timeout: Long = 0): Long {
    write(value)
    return flush(timeout)
  }
}