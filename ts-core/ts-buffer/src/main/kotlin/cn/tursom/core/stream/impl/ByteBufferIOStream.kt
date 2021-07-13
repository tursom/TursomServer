package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.IOStream
import cn.tursom.core.stream.SuspendInputStream
import cn.tursom.core.stream.SuspendOutputStream
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ByteBufferIOStream(
  private val buffer: ByteBuffer,
  private val lock: Lock?,
  val inputStream: ByteBufferInputStream = ByteBufferInputStream(buffer, lock),
  val outputStream: ByteBufferOutputStream = ByteBufferOutputStream(buffer, lock),
) : IOStream,
  SuspendInputStream by inputStream,
  SuspendOutputStream by outputStream {
  constructor(
    buffer: ByteBuffer,
    lock: Boolean = true,
  ) : this(
    buffer,
    if (lock) ReentrantLock() else null,
    ByteBufferInputStream(buffer),
    ByteBufferOutputStream(buffer)
  )

  override fun close() {
    buffer.close()
    inputStream.close()
    outputStream.close()
  }
}