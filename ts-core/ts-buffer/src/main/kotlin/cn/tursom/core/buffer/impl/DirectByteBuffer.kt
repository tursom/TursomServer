package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer

class DirectByteBuffer(
  private var buffer: java.nio.ByteBuffer,
  override var readPosition: Int = 0,
  override var writePosition: Int = 0
) : ByteBuffer {
  constructor(size: Int) : this(java.nio.ByteBuffer.allocateDirect(size))

  override val hasArray: Boolean = false
  override val array: ByteArray get() = buffer.array()
  override val capacity: Int get() = buffer.capacity()
  override val arrayOffset: Int = 0

  override var resized: Boolean = false

  override fun readBuffer(): java.nio.ByteBuffer {
    if (buffer.limit() != writePosition)
      buffer.limit(writePosition)
    if (buffer.position() != readPosition)
      buffer.position(readPosition)
    return buffer.slice()
  }

  override fun writeBuffer(): java.nio.ByteBuffer {
    if (buffer.limit() != capacity)
      buffer.limit(capacity)
    if (buffer.position() != writePosition)
      buffer.position(writePosition)
    return buffer.slice()
  }

  override fun reset() {
    if (readPosition == 0) return
    if (writePosition == readPosition) {
      readPosition = 0
      writePosition = 0
    } else {
      buffer.limit(writePosition)
      buffer.position(readPosition)
      buffer.compact()
      readPosition = 0
      writePosition = buffer.position()
    }
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    buffer.limit(position + size)
    buffer.position(position)
    return DirectByteBuffer(buffer.slice(), readPosition, writePosition)
  }

  override fun resize(newSize: Int): Boolean {
    if (newSize <= buffer.capacity()) return false
    resized = true
    val newBuf = java.nio.ByteBuffer.allocateDirect(newSize)
    newBuf.put(readBuffer())
    buffer = newBuf
    writePosition = readable
    readPosition = 0
    return true
  }

  override fun toString(): String {
    return "DirectByteBuffer(buffer=$buffer, hasArray=$hasArray, capacity=$capacity, writePosition=$writePosition, readPosition=$readPosition)"
  }

  override fun fill(byte: Byte) {
    readPosition = 0
    writePosition = 0
    buffer.clear()
    while (buffer.remaining() != 0) {
      buffer.put(byte)
    }
  }
}