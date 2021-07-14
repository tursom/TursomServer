package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer

class ArrayByteBuffer(
  vararg buffers: ByteBuffer,
) : ListByteBuffer(ArrayList(buffers.asList()))