package cn.tursom.channel.enhance

import java.io.Closeable

interface SocketWriter<T> : Closeable {
  suspend fun put(value: T, timeout: Long) {
    put(value)
    flush(timeout)
  }

  suspend fun put(value: T)
  suspend fun flush(timeout: Long = 0)
}