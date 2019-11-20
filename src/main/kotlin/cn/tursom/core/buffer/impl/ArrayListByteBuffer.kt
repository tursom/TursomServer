package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.buffer.MultipleByteBuffer

class ArrayListByteBuffer : MultipleByteBuffer, MutableList<ByteBuffer> by ArrayList() {
  override fun clear() = super.clear()
}