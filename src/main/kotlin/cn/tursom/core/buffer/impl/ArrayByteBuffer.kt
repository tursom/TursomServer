package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.buffer.MultipleByteBuffer

class ArrayByteBuffer(
    override vararg val buffers: ByteBuffer
) : MultipleByteBuffer, List<ByteBuffer> by buffers.asList()