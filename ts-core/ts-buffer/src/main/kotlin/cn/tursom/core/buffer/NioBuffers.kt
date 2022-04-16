package cn.tursom.core.buffer


object NioBuffers {

  interface Arrays {
    fun readBufferArray(): Array<out java.nio.ByteBuffer>
    fun finishRead(buffers: Array<out java.nio.ByteBuffer>): Long

    fun writeBufferArray(): Array<out java.nio.ByteBuffer>
    fun finishWrite(buffers: Array<out java.nio.ByteBuffer>): Long
  }

  inline fun <T> Arrays.readNioBuffers(action: (bufferList: Array<out java.nio.ByteBuffer>) -> T): T {
    val buffers = readBufferArray()
    return try {
      action(buffers)
    } finally {
      finishRead(buffers)
    }
  }

  inline fun <T> Arrays.writeNioBuffers(action: (bufferList: Array<out java.nio.ByteBuffer>) -> T): T {
    val buffers = writeBufferArray()
    return try {
      action(buffers)
    } finally {
      finishWrite(buffers)
    }
  }

  inline fun <T> Array<out ByteBuffer>.readNioBuffers(
    offset: Int,
    length: Int,
    action: (bufferList: Array<out java.nio.ByteBuffer>) -> T,
  ): T {
    val bufferList = getReadNioBufferList(offset, length)
    try {
      return action(bufferList)
    } finally {
      finishRead(bufferList, offset)
    }
  }

  inline fun <T> Array<out ByteBuffer>.writeNioBuffers(
    offset: Int,
    length: Int,
    action: (bufferList: Array<out java.nio.ByteBuffer>) -> T,
  ): T {
    val bufferList = getWriteNioBufferList(offset, length)
    try {
      return action(bufferList)
    } finally {
      finishWrite(bufferList, offset)
    }
  }

  fun Array<out ByteBuffer>.getReadNioBufferList(
    offset: Int = 0,
    length: Int = size - offset,
  ): Array<out java.nio.ByteBuffer> = Array(length) {
    get(offset + it).readBuffer()
  }

  fun Array<out ByteBuffer>.finishRead(buffers: Array<out java.nio.ByteBuffer>, offset: Int): Long {
    var readed = 0L
    buffers.forEachIndexed { index, byteBuffer ->
      readed += get(offset + index).finishRead(byteBuffer)
    }
    return readed
  }

  fun Array<out ByteBuffer>.getWriteNioBufferList(
    offset: Int = 0,
    length: Int = size - offset,
  ): Array<out java.nio.ByteBuffer> = Array(length) {
    get(offset + it).writeBuffer()
  }

  fun Array<out ByteBuffer>.finishWrite(buffers: Array<out java.nio.ByteBuffer>, offset: Int): Long {
    var written = 0L
    buffers.forEachIndexed { index, byteBuffer ->
      written += get(offset + index).finishWrite(byteBuffer)
    }
    return written
  }
}