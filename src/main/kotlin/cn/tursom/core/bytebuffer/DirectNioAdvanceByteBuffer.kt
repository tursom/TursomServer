package cn.tursom.core.bytebuffer

import cn.tursom.core.logE
import java.nio.ByteBuffer

class DirectNioAdvanceByteBuffer(val buffer: ByteBuffer) : AdvanceByteBuffer {
  override val nioBuffer: ByteBuffer get() = buffer
  override val readOnly: Boolean get() = buffer.isReadOnly
  var writeMark = 0
  override var writePosition: Int
    get() {
      return if (readMode) writeMark
      else buffer.position()
    }
    set(value) {
      if (!readMode) buffer.position(value)
      else buffer.limit(value)
    }
  override var limit: Int = buffer.limit()
    get() = if (!readMode) buffer.limit() else field
    set(value) {
      if (!readMode) buffer.limit(value)
      field = value
    }
  override val capacity: Int get() = buffer.capacity()
  
  override val hasArray: Boolean get() = false
  override val array: ByteArray get() = buffer.array()
  override val arrayOffset: Int = 0
  override var readPosition: Int = 0
    get() = if (readMode) buffer.position() else field
    set(value) {
      if (readMode) buffer.position(value)
      field = value
    }
  override val readableSize: Int get() = if (readMode) buffer.remaining() else writePosition - readPosition
  override val size: Int get() = buffer.capacity()
  override var readMode: Boolean = false
  
  override fun readMode() {
    if (!readMode) {
      writeMark = writePosition
      //logE("readMode() $this $writeMark $writePosition ${buffer.position()}")
      readMode = true
      buffer.flip()
      buffer.position(readPosition)
      //logE("readMode() $this $writeMark $writePosition ${buffer.position()}")
    }
  }
  
  override fun resumeWriteMode(usedSize: Int) {
    if (readMode) {
      readMode = false
      buffer.limit(capacity)
      buffer.position(writeMark)
    }
  }
  
  override fun clear() {
    resumeWriteMode()
    buffer.clear()
    readPosition = 0
  }
  
  override fun toString(): String {
    return "DirectNioAdvanceByteBuffer(buffer=$buffer, readMode=$readMode, readPosition=$readPosition, writePosition=$writePosition)"
  }
}