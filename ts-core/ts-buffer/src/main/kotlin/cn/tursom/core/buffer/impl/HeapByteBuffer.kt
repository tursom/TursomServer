package cn.tursom.core.buffer.impl

import cn.tursom.core.ByteBufferUtil
import cn.tursom.core.buffer.ByteBuffer

class HeapByteBuffer(
  private var buffer: java.nio.ByteBuffer,
  override var readPosition: Int = 0,
  override var writePosition: Int = 0,
) : ByteBuffer {
  constructor(size: Int) : this(java.nio.ByteBuffer.allocate(size))
  constructor(string: String) : this(string.toByteArray())
  constructor(bytes: ByteArray, offset: Int = 0, size: Int = bytes.size - offset)
    : this(ByteBufferUtil.wrap(bytes, offset, size), offset, offset + size)

  constructor(buffer: java.nio.ByteBuffer, write: Boolean) : this(
    buffer,
    if (write) 0 else buffer.position(),
    if (write) buffer.position() else buffer.limit()
  )

  constructor(bytes: ByteArray, write: Boolean) : this(
    java.nio.ByteBuffer.wrap(bytes),
    0,
    if (write) 0 else bytes.size
  )

  init {
    assert(buffer.hasArray())
  }

  override val hasArray: Boolean = true
  override val array: ByteArray get() = buffer.array()
  override val capacity: Int get() = buffer.capacity()
  override val arrayOffset: Int get() = buffer.arrayOffset()

  override var resized: Boolean = false

  override fun readBuffer(): java.nio.ByteBuffer {
    if (buffer.limit() != writePosition)
      buffer.limit(writePosition)
    if (buffer.position() != readPosition)
      buffer.position(readPosition)
    return buffer
  }

  override fun finishRead(buffer: java.nio.ByteBuffer): Int {
    val rp = readPosition
    if (buffer === this.buffer) {
      readPosition = buffer.position()
    }
    return readPosition - rp
  }

  override fun writeBuffer(): java.nio.ByteBuffer {
    if (buffer.limit() != capacity)
      buffer.limit(capacity)
    if (buffer.position() != writePosition)
      buffer.position(writePosition)
    return buffer
  }

  override fun finishWrite(buffer: java.nio.ByteBuffer): Int {
    val wp = writePosition
    if (buffer === this.buffer) {
      writePosition = buffer.position()
    }
    return writePosition - wp
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
    return HeapByteBuffer(buffer.slice(), readPosition, writePosition)
  }

  override fun resize(newSize: Int): Boolean {
    if (newSize <= buffer.capacity()) return false
    resized = true
    val newBuf = java.nio.ByteBuffer.allocate(newSize)
    newBuf.put(array, readOffset, readable)
    buffer = newBuf
    writePosition = readable
    readPosition = 0
    return true
  }

  override fun toString(): String {
    return "HeapByteBuffer(buffer=$buffer, hasArray=$hasArray, capacity=$capacity, arrayOffset=$arrayOffset, writePosition=$writePosition, readPosition=$readPosition)"
  }

  override fun fill(byte: Byte) {
    writePosition = 0
    readPosition = 0
    array.fill(byte, arrayOffset, arrayOffset + capacity)
  }

  companion object {
    fun ByteArray.toByteBuffer() = HeapByteBuffer(this, 0, size)
  }
}