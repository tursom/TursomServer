package cn.tursom.core.bytebuffer

import cn.tursom.core.*
import java.io.OutputStream
import java.nio.ByteBuffer

@Suppress("unused", "MemberVisibilityCanBePrivate")
class HeapNioAdvanceByteBuffer(val buffer: ByteBuffer) : AdvanceByteBuffer {
  constructor(size: Int) : this(ByteBuffer.allocate(size))
  constructor(buffer: ByteArray, offset: Int = 0, size: Int = buffer.size - offset) : this(HeapByteBuffer.wrap(buffer, offset, size))
  
  override val nioBuffer: ByteBuffer get() = buffer
  
  override val hasArray: Boolean get() = buffer.hasArray()
  override val readOnly: Boolean get() = buffer.isReadOnly
  
  private var _readMode = false
  var readMark = 0
  var writeMark = 0
  
  /**
   * 各种位置变量
   */
  override var writePosition
    get() = buffer.position()
    set(value) {
      buffer.position(value)
    }
  override var limit
    get() = buffer.limit()
    set(value) {
      buffer.limit(value)
    }
  
  override val capacity: Int = buffer.capacity()
  override val array: ByteArray get() = buffer.array()
  override val arrayOffset: Int get() = buffer.arrayOffset()
  override var readPosition: Int = 0
  override val readOffset get() = arrayOffset + readPosition
  override val readableSize get() = writePosition - readPosition
  override val available get() = readableSize
  override val writeOffset get() = arrayOffset + writePosition
  override val writeableSize get() = limit - writePosition
  override val size = buffer.capacity()
  override val readMode get() = _readMode
  
  /*
   * 位置控制方法
   */
  
  override fun readMode() {
    writeMark = buffer.position()
    readMark = readPosition
    buffer.limit(buffer.position())
    buffer.position(readPosition)
    _readMode = true
  }
  
  override fun resumeWriteMode(usedSize: Int) {
    readPosition = readMark + usedSize
    buffer.limit(buffer.capacity())
    buffer.position(writeMark)
    _readMode = false
  }
  
  override fun needReadSize(size: Int) {
    if (readableSize < size) throw OutOfBufferException()
  }
  
  override fun useReadSize(size: Int): Int {
    needReadSize(size)
    readPosition += size
    return size
  }
  
  override fun take(size: Int): Int {
    needReadSize(size)
    val offset = readOffset
    readPosition += size
    return offset
  }
  
  override fun push(size: Int): Int {
    val offset = writeOffset
    writePosition += size
    return offset
  }
  
  override fun readAllSize() = useReadSize(readableSize)
  override fun takeAll() = take(readableSize)
  
  override fun clear() {
    readPosition = 0
    buffer.clear()
  }
  
  override fun reset() {
    array.copyInto(array, arrayOffset, readOffset, arrayOffset + writePosition)
    writePosition = readableSize
    readPosition = 0
  }
  
  override fun reset(outputStream: OutputStream) {
    outputStream.write(array, readOffset, arrayOffset + writePosition)
    writePosition = 0
    readPosition = 0
  }
  
  override fun requireAvailableSize(size: Int) {
    if (limit - readPosition < size) reset()
  }
  
  
  /*
   * 数据获取方法
   */
  
  override fun get() = array[take(1)]
  override fun getChar() = array.toChar(take(2))
  override fun getShort() = array.toShort(take(2))
  override fun getInt() = array.toInt(take(4))
  override fun getLong() = array.toLong(take(8))
  override fun getFloat() = array.toFloat(take(4))
  override fun getDouble() = array.toDouble(take(8))
  override fun getBytes(size: Int): ByteArray {
    val readMode = readMode
    readMode()
    val array = array.copyOfRange(readPosition, useReadSize(size))
    if (!readMode) resumeWriteMode(size)
    return array
  }
  
  override fun getString(size: Int) = String(array, readOffset, useReadSize(size))
  
  override fun writeTo(buffer: ByteArray, bufferOffset: Int, size: Int): Int {
    array.copyInto(buffer, bufferOffset, arrayOffset, useReadSize(size))
    return size
  }
  
  override fun toByteArray() = getBytes()
  
  
  /*
   * 数据写入方法
   */
  
  override fun put(byte: Byte) {
    buffer.put(byte)
  }
  
  override fun put(char: Char) = array.put(char, push(2))
  override fun put(short: Short) = array.put(short, push(2))
  override fun put(int: Int) = array.put(int, push(4))
  override fun put(long: Long) = array.put(long, push(8))
  override fun put(float: Float) = array.put(float, push(4))
  override fun put(double: Double) = array.put(double, push(8))
  override fun put(str: String) = put(str.toByteArray())
  override fun put(byteArray: ByteArray, startIndex: Int, endIndex: Int) {
    byteArray.copyInto(array, push(endIndex - startIndex), startIndex, endIndex)
  }
  
  override fun split(from: Int, to: Int): AdvanceByteBuffer {
    val readMark = readPosition
    val writeMark = writePosition
    buffer.position(readMark)
    buffer.limit(writeMark)
    val slice = HeapNioAdvanceByteBuffer(buffer.slice())
    readPosition = readMark
    writePosition = writeMark
    return slice
  }
  
  override fun toString(): String {
    return "HeapNioAdvanceByteBuffer(buffer=$buffer, readMode=$_readMode, readMark=$readMark, writeMark=$writeMark, capacity=$capacity, readPosition=$readPosition, size=$size)"
  }
}