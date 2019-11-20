package cn.tursom.socket.enhance

import java.io.Closeable

interface EnhanceSocket<Read, Write> : SocketReader<Read>, SocketWriter<Write>, Closeable {
  override fun close()
}