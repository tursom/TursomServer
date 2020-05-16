package cn.tursom.utils.bytebuffer

import cn.tursom.core.buffer.ByteBuffer
import io.netty.buffer.ByteBuf
import java.io.OutputStream

class NettyByteBuffer(
  val byteBuf: ByteBuf
) : ByteBuffer {
  constructor(
    byteBuf: ByteBuf,
    readPosition: Int = byteBuf.readerIndex(),
    writePosition: Int = byteBuf.writerIndex()
  ) : this(byteBuf) {
    this.writePosition = writePosition
    this.readPosition = readPosition
  }

  override val hasArray: Boolean get() = byteBuf.hasArray()
  override var writePosition: Int
    get() = byteBuf.writerIndex()
    set(value) {
      byteBuf.writerIndex(value)
    }
  override val capacity get() = byteBuf.maxCapacity()
  override val array: ByteArray get() = byteBuf.array()
  override val arrayOffset: Int get() = byteBuf.arrayOffset()
  override var readPosition: Int
    get() = byteBuf.readerIndex()
    set(value) {
      byteBuf.readerIndex(value)
    }
  override val resized: Boolean get() = false
  override var closed: Boolean = false

  override fun readBuffer(): java.nio.ByteBuffer {
    return byteBuf.internalNioBuffer(readPosition, readable).slice()
  }

  override fun writeBuffer(): java.nio.ByteBuffer {
    return byteBuf.internalNioBuffer(writePosition, writeable).slice()
  }

  override val readOffset: Int get() = byteBuf.arrayOffset() + byteBuf.readerIndex()

  override fun clear() {
    byteBuf.clear()
  }

  override fun reset() {
    byteBuf.discardReadBytes()
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer {
    return NettyByteBuffer(byteBuf.slice(position, size), readPosition, writePosition)
  }

  override fun resize(newSize: Int): Boolean {
    return false
  }

  override fun get(): Byte = byteBuf.readByte()
  override fun getChar(): Char = byteBuf.readChar()
  override fun getShort(): Short = byteBuf.readShort()
  override fun getInt(): Int = byteBuf.readInt()
  override fun getLong(): Long = byteBuf.readLong()
  override fun getFloat(): Float = byteBuf.readFloat()
  override fun getDouble(): Double = byteBuf.readDouble()

  override fun getBytes(size: Int): ByteArray {
    val bytes = ByteArray(size)
    byteBuf.readBytes(bytes)
    return bytes
  }

  override fun getString(size: Int): String {
    val str = byteBuf.toString(readPosition, size, Charsets.UTF_8)
    readPosition += size
    return str
  }

  override fun writeTo(buffer: ByteArray, bufferOffset: Int, size: Int): Int {
    byteBuf.readBytes(buffer, bufferOffset, size)
    return size
  }

  override fun writeTo(os: OutputStream): Int {
    val size = readable
    byteBuf.readBytes(os, size)
    reset()
    return size
  }

  override fun put(byte: Byte) {
    byteBuf.writeByte(byte.toInt())
  }

  override fun put(char: Char) {
    byteBuf.writeChar(char.toInt())
  }

  override fun put(short: Short) {
    byteBuf.writeShort(short.toInt())
  }

  override fun put(int: Int) {
    byteBuf.writeInt(int)
  }

  override fun put(long: Long) {
    byteBuf.writeLong(long)
  }

  override fun put(float: Float) {
    byteBuf.writeFloat(float)
  }

  override fun put(double: Double) {
    byteBuf.writeDouble(double)
  }

  override fun put(str: String) {
    byteBuf.writeCharSequence(str, Charsets.UTF_8)
  }

  override fun put(byteArray: ByteArray, offset: Int, len: Int) {
    byteBuf.writeBytes(byteArray, offset, len - offset)
  }

  override fun toString(): String {
    return "Nettyjava.nio.ByteBuffer(byteBuf=$byteBuf)"
  }

  override fun close() {
    if (closed) {
      closed = true
      byteBuf.release()
    }
  }
}