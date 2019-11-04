package cn.tursom.core.bytebuffer

import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

class MultiAdvanceByteBuffer(vararg val buffers: AdvanceByteBuffer) : AdvanceByteBuffer {
  init {
    resumeWriteMode()
  }
  
  var writeBufferIndex = 0
  var readBufferIndex = 0
  val readBuffer get() = buffers[writeBufferIndex]
  val writeBuffer get() = buffers[writeBufferIndex]
  
  val operatorBuffer
    get() = if (readMode) {
      readBuffer
    } else {
      writeBuffer
    }
  
  override val nioBuffers: Array<out ByteBuffer>
    get() {
      val bufList = ArrayList<ByteBuffer>()
      buffers.forEach { buffer ->
        if (buffer.bufferCount == 1) {
          bufList.add(buffer.nioBuffer)
        } else {
          buffer.nioBuffers.forEach {
            bufList.add(it)
          }
        }
      }
      return bufList.toTypedArray()
    }
  override val hasArray: Boolean get() = false
  override val readOnly: Boolean get() = false
  override val bufferCount: Int get() = buffers.size
  
  override val nioBuffer: ByteBuffer get() = operatorBuffer.nioBuffer
  override var writePosition: Int
    get() = operatorBuffer.writePosition
    set(value) {
      operatorBuffer.writePosition = value
    }
  override var limit: Int
    get() = operatorBuffer.limit
    set(value) {
      operatorBuffer.limit = value
    }
  override val capacity: Int get() = operatorBuffer.capacity
  override val array: ByteArray get() = operatorBuffer.array
  override val arrayOffset: Int get() = operatorBuffer.arrayOffset
  override var readPosition: Int
    get() = operatorBuffer.readPosition
    set(value) {
      operatorBuffer.readPosition = value
    }
  override val readOffset: Int get() = operatorBuffer.readOffset
  override val readableSize: Int get() = operatorBuffer.readableSize
  override val available: Int get() = operatorBuffer.available
  override val writeOffset: Int get() = operatorBuffer.writeOffset
  override val writeableSize: Int get() = operatorBuffer.writeableSize
  override val size: Int get() = operatorBuffer.size
  override var readMode: Boolean = false
  
  override fun readMode() {
    readMode = true
    buffers.forEach(AdvanceByteBuffer::readMode)
  }
  
  override fun resumeWriteMode(usedSize: Int) {
    readMode = false
    buffers.forEach { it.resumeWriteMode() }
  }
  
  override fun clear() {
    writeBufferIndex = 0
    readBufferIndex = 0
    buffers.forEach { buffer -> buffer.clear() }
  }
  
  override fun get(): Byte = readBuffer.get()
  override fun getChar(): Char = readBuffer.getChar()
  override fun getShort(): Short = readBuffer.getShort()
  override fun getInt(): Int = readBuffer.getInt()
  override fun getLong(): Long = readBuffer.getLong()
  override fun getFloat(): Float = readBuffer.getFloat()
  override fun getDouble(): Double = readBuffer.getDouble()
  override fun getBytes(size: Int) = readBuffer.getBytes(size)
  override fun getString(size: Int): String = readBuffer.getString(size)
  
  override fun put(byte: Byte) = writeBuffer.put(byte)
  override fun put(char: Char) = writeBuffer.put(char)
  override fun put(short: Short) = writeBuffer.put(short)
  override fun put(int: Int) = writeBuffer.put(int)
  override fun put(long: Long) = writeBuffer.put(long)
  override fun put(float: Float) = writeBuffer.put(float)
  override fun put(double: Double) = writeBuffer.put(double)
  override fun put(str: String) = writeBuffer.put(str)
  override fun toString(): String {
    return "MultiAdvanceByteBuffer(buffers=${Arrays.toString(buffers)}, writeBufferIndex=$writeBufferIndex, readBufferIndex=$readBufferIndex, readMode=$readMode)"
  }
  
  
}