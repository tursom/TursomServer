package cn.tursom.core.buffer.impl

import cn.tursom.core.AsyncFile
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.reference.FreeReference
import cn.tursom.log.impl.Slf4jImpl
import io.netty.buffer.ByteBuf
import java.io.OutputStream
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.suspendCoroutine

class NettyByteBuffer(
  val byteBuf: ByteBuf,
  autoClose: Boolean = false,
) : ByteBuffer {
  companion object : Slf4jImpl()

  constructor(
    byteBuf: ByteBuf,
    readPosition: Int = byteBuf.readerIndex(),
    writePosition: Int = byteBuf.writerIndex(),
    autoClose: Boolean = false,
  ) : this(byteBuf, autoClose) {
    this.writePosition = writePosition
    this.readPosition = readPosition
  }

  internal class AutoFreeReference(
    nettyByteBuffer: NettyByteBuffer,
    private val atomicClosed: AtomicBoolean,
    private val byteBuf: ByteBuf,
  ) : FreeReference<NettyByteBuffer>(nettyByteBuffer) {
    override fun release() {
      if (atomicClosed.compareAndSet(false, true)) {
        byteBuf.release()
      }
    }
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
  override val closed get() = atomicClosed.get()
  override val isReadable get() = byteBuf.isReadable
  override val isWriteable get() = byteBuf.isWritable

  private val atomicClosed = AtomicBoolean(false)

  override val fileReader: AsyncFile.Reader = object : AsyncFile.Reader {
    override suspend fun read(file: AsyncFile, position: Long): Int {
      val nioBuffers = byteBuf.nioBuffers(byteBuf.writerIndex(), byteBuf.capacity())
      var readPosition = position
      for (nioBuffer in nioBuffers) {
        while (nioBuffer.hasRemaining()) {
          val readSize = suspendCoroutine<Int> { cont ->
            file.writeChannel.read(nioBuffer, readPosition, cont, AsyncFile.handler)
          }
          if (readSize <= 0) break
          readPosition += readSize
          byteBuf.writerIndex(byteBuf.writerIndex() + readSize)
        }
      }
      return (readPosition - position).toInt()
    }
  }

  override val fileWriter: AsyncFile.Writer = object : AsyncFile.Writer {
    override suspend fun writeAndWait(file: AsyncFile, position: Long): Int {
      val nioBuffers = byteBuf.nioBuffers()
      var writePosition = position
      for (nioBuffer in nioBuffers) {
        while (nioBuffer.hasRemaining()) {
          val writeSize = suspendCoroutine<Int> { cont ->
            file.writeChannel.write(nioBuffer, writePosition, cont, AsyncFile.handler)
          }
          if (writeSize <= 0) break
          writePosition += writeSize
          byteBuf.readerIndex(byteBuf.readerIndex() + writeSize)
        }
      }
      return (writePosition - position).toInt()
    }
  }

  private val reference = if (autoClose) {
    AutoFreeReference(this, atomicClosed, byteBuf)
  } else {
    null
  }

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
    return NettyByteBuffer(byteBuf.retainedSlice(position, size), readPosition, writePosition, reference != null)
  }

  override fun resize(newSize: Int): Boolean {
    return false
  }

  override fun get(): Byte = byteBuf.readByte()
  override fun getChar(byteOrder: ByteOrder): Char = byteBuf.readChar()
  override fun getShort(byteOrder: ByteOrder): Short = byteBuf.readShort()
  override fun getInt(byteOrder: ByteOrder): Int = byteBuf.readInt()
  override fun getLong(byteOrder: ByteOrder): Long = byteBuf.readLong()
  override fun getFloat(byteOrder: ByteOrder): Float = byteBuf.readFloat()
  override fun getDouble(byteOrder: ByteOrder): Double = byteBuf.readDouble()

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

  override fun writeTo(os: OutputStream, buffer: ByteArray?): Int {
    val size = readable
    byteBuf.readBytes(os, size)
    reset()
    return size
  }

  override fun put(byte: Byte) {
    byteBuf.writeByte(byte.toInt())
  }

  override fun put(char: Char) {
    byteBuf.writeChar(char.code)
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

  override fun put(str: String): Int {
    return byteBuf.writeCharSequence(str, Charsets.UTF_8)
  }

  override fun put(byteArray: ByteArray, offset: Int, len: Int): Int {
    val writePosition = byteBuf.writerIndex()
    byteBuf.writeBytes(byteArray, offset, len - offset)
    return byteBuf.writerIndex() - writePosition
  }

  override fun close() {
    if (atomicClosed.compareAndSet(false, true)) {
      byteBuf.release()
      reference?.cancel()
    }
  }

  override fun toString(): String {
    return "Nettyjava.nio.ByteBuffer(byteBuf=$byteBuf)"
  }
}