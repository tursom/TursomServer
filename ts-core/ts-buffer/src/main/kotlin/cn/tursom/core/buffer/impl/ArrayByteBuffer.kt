package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer

class ArrayByteBuffer(
  override vararg val buffers: ByteBuffer
) : ListByteBuffer(buffers.asList())