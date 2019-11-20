package cn.tursom.socket.enhance

import cn.tursom.core.buffer.ByteBuffer
import java.io.Closeable

interface SocketReader<T> : Closeable {
  suspend fun get(buffer: ByteBuffer, timeout: Long = 0): T
  override fun close()
}

