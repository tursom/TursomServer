package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer

class ArrayByteBuffer(
  vararg buffers: ByteBuffer,
) : ListByteBuffer(ArrayList(buffers.asList())) {
  override fun toString(): String {
    return "ArrayByteBuffer[$readPosition:$writePosition:$capacity]"
  }
}