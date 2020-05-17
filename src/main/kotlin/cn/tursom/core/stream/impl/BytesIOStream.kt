package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.stream.IOStream
import cn.tursom.core.stream.SuspendInputStream

class BytesIOStream private constructor(
  private val buBufferIOStream: ByteBufferIOStream
) : IOStream by buBufferIOStream, SuspendInputStream {
  constructor(bytes: ByteArray) : this(ByteBufferIOStream(HeapByteBuffer(bytes).apply { clear() }))

  override fun skip(n: Long, handler: () -> Unit) = buBufferIOStream.skip(n, handler)
  override fun read(handler: (Int) -> Unit) = buBufferIOStream.read(handler)
  override fun read(buffer: ByteBuffer, handler: () -> Unit) = buBufferIOStream.read(buffer, handler)
  override fun read(buffer: ByteArray, handler: (Int) -> Unit) = buBufferIOStream.read(buffer, handler)
  override fun read(buffer: ByteArray, offset: Int, len: Int, handler: (Int) -> Unit) =
    buBufferIOStream.read(buffer, offset, len, handler)
}