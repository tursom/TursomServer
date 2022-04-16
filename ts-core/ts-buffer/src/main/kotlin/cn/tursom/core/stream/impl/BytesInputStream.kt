package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.stream.InputStream

class BytesInputStream(
  val bytes: ByteArray,
  offset: Int = 0,
  len: Int = bytes.size - offset,
) : InputStream by ByteBufferInputStream(HeapByteBuffer(bytes, offset, len))