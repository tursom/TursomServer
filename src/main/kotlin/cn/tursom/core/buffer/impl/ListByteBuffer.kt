package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.buffer.MultipleByteBuffer

class ListByteBuffer(bufferList: List<ByteBuffer>) : MultipleByteBuffer, List<ByteBuffer> by bufferList