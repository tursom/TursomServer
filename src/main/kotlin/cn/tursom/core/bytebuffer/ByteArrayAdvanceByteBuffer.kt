package cn.tursom.core.bytebuffer

import cn.tursom.core.*
import java.io.OutputStream
import java.nio.ByteBuffer

class ByteArrayAdvanceByteBuffer(
    override val array: ByteArray,
    val offset: Int = 0,
    override val size: Int = array.size - offset,
    override var readPosition: Int = 0,
    override var writePosition: Int = size
) : AdvanceByteBuffer {
  constructor(size: Int) : this(ByteArray(size), 0, size, 0, 0)
  
  override val hasArray: Boolean get() = true
  override var readOnly: Boolean = false
  override val nioBuffer: ByteBuffer
    get() = if (readMode) readByteBuffer
    else writeByteBuffer
  override var limit: Int = size
  override val capacity: Int get() = size
  override val arrayOffset: Int get() = offset
  override val available: Int
    get() = readableSize
  override val writeableSize: Int get() = limit - writePosition
  override var readMode: Boolean = false
  
  override fun readMode() {
    readMode = true
  }
  
  override fun resumeWriteMode(usedSize: Int) {
    readPosition += usedSize
    readMode = false
  }
  
  override fun writeTo(os: OutputStream): Int {
    val size = readableSize
    os.write(array, readOffset, size)
    reset()
    return size
  }
  
  
  override val readOffset get() = offset + readPosition
  override val writeOffset get() = offset + writePosition
  
  val readByteBuffer get() = HeapByteBuffer.wrap(array, offset + readPosition, writePosition - readPosition)
  val writeByteBuffer get() = HeapByteBuffer.wrap(array, offset + writePosition, limit - writePosition)
  
  override val readableSize get() = writePosition - readPosition
  
  val position get() = "ArrayByteBuffer(size=$size, writePosition=$writePosition, readPosition=$readPosition)"
  
  /*
   * 位置控制方法
   */
  
  override fun clear() {
    writePosition = 0
    readPosition = 0
  }
  
  override fun reset() {
    array.copyInto(array, offset, readOffset, offset + writePosition)
    writePosition = readableSize
    readPosition = 0
  }
  
  override fun reset(outputStream: OutputStream) {
    outputStream.write(array, readOffset, offset + writePosition)
    writePosition = 0
    readPosition = 0
  }
  
  override fun needReadSize(size: Int) {
    if (readableSize < size) throw OutOfBufferException()
  }
  
  override fun take(size: Int): Int {
    needReadSize(size)
    val offset = readOffset
    readPosition += size
    return offset
  }
  
  override fun useReadSize(size: Int): Int {
    needReadSize(size)
    readPosition += size
    return size
  }
  
  override fun push(size: Int): Int {
    val offset = writeOffset
    writePosition += size
    return offset
  }
  
  override fun readAllSize() = useReadSize(readableSize)
  override fun takeAll() = take(readableSize)
  
  
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
  
  override fun getString(size: Int) = String(array, readPosition, useReadSize(size))
  
  override fun writeTo(buffer: ByteArray, bufferOffset: Int, size: Int): Int {
    array.copyInto(buffer, bufferOffset, offset, useReadSize(size))
    return size
  }
  
  override fun toByteArray() = getBytes()
  
  
  /*
   * 数据写入方法
   */
  
  override fun put(byte: Byte) {
    array.put(byte, push(1))
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
  
  override fun toString(): String {
    //return String(array, readOffset, readableSize)
    return "ByteArrayAdvanceByteBuffer(size=$size, readPosition=$readPosition, writePosition=$writePosition)"
  }
  
  /**
   * 缓冲区用完异常
   */
  class OutOfBufferException : Exception()
}