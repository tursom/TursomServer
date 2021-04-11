package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.stream.OutputStream

class BytesOutputStream(
  val bytes: ByteArray,
  offset: Int = 0,
  len: Int = bytes.size - offset
) : OutputStream by ByteBufferOutputStream(HeapByteBuffer(bytes, offset, len).apply { clear() })