package cn.tursom.core.buffer.impl

import cn.tursom.core.AsyncFile
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.ByteBufferExtensionKey
import cn.tursom.core.buffer.NioBuffers
import cn.tursom.core.reference.FreeReference
import cn.tursom.core.uncheckedCast
import cn.tursom.log.impl.Slf4jImpl
import io.netty.buffer.ByteBuf
import java.io.OutputStream
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.suspendCoroutine

class NettyByteBuffer(
  val byteBuf: ByteBuf,
  autoClose: Boolean = false,
) : ByteBuffer, AsyncFile.Reader, AsyncFile.Writer, NioBuffers.Arrays {
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

  private var nioReadPosition = 0
  private var nioReadBuffersNum = 0
  private var nioWritePosition = 0
  private var nioWriteBuffersNum = 0

  override fun <T> getExtension(key: ByteBufferExtensionKey<T>): T? {
    return when (key) {
      AsyncFile.Reader, AsyncFile.Writer -> this.uncheckedCast()
      else -> super.getExtension(key)
    }
  }

  override suspend fun readAsyncFile(file: AsyncFile, position: Long): Int {
    val nioBuffers = byteBuf.nioBuffers(byteBuf.writerIndex(), byteBuf.writableBytes())
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

  override suspend fun writeAsyncFile(file: AsyncFile, position: Long): Int {
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

  private val reference = if (autoClose) {
    AutoFreeReference(this, atomicClosed, byteBuf)
  } else {
    null
  }

  override fun readBuffer(): java.nio.ByteBuffer {
    return byteBuf.internalNioBuffer(readPosition, readable).slice()
  }

  override fun finishRead(buffer: java.nio.ByteBuffer) {
    byteBuf.readerIndex(buffer.position())
  }

  override fun writeBuffer(): java.nio.ByteBuffer {
    return byteBuf.internalNioBuffer(writePosition, writeable).slice()
  }

  override fun finishWrite(buffer: java.nio.ByteBuffer) {
    byteBuf.writerIndex(buffer.position())
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

  override fun getChar(byteOrder: ByteOrder): Char = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteBuf.readChar()
    ByteOrder.LITTLE_ENDIAN -> byteBuf.readShortLE().toInt().toChar()
    else -> throw IllegalArgumentException("")
  }

  override fun getShort(byteOrder: ByteOrder): Short = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteBuf.readShort()
    ByteOrder.LITTLE_ENDIAN -> byteBuf.readShortLE()
    else -> throw IllegalArgumentException("")
  }

  override fun getInt(byteOrder: ByteOrder): Int = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteBuf.readInt()
    ByteOrder.LITTLE_ENDIAN -> byteBuf.readIntLE()
    else -> throw IllegalArgumentException("")
  }

  override fun getLong(byteOrder: ByteOrder): Long = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteBuf.readLong()
    ByteOrder.LITTLE_ENDIAN -> byteBuf.readLongLE()
    else -> throw IllegalArgumentException("")
  }

  override fun getFloat(byteOrder: ByteOrder): Float = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteBuf.readFloat()
    ByteOrder.LITTLE_ENDIAN -> byteBuf.readFloatLE()
    else -> throw IllegalArgumentException("")
  }

  override fun getDouble(byteOrder: ByteOrder): Double = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteBuf.readDouble()
    ByteOrder.LITTLE_ENDIAN -> byteBuf.readDoubleLE()
    else -> throw IllegalArgumentException("")
  }

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

  override fun put(char: Char, byteOrder: ByteOrder) {
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> byteBuf.writeChar(char.code)
      ByteOrder.LITTLE_ENDIAN -> byteBuf.writeShortLE(char.code)
      else -> throw IllegalArgumentException()
    }
  }

  override fun put(short: Short, byteOrder: ByteOrder) {
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> byteBuf.writeShort(short.toInt())
      ByteOrder.LITTLE_ENDIAN -> byteBuf.writeShortLE(short.toInt())
      else -> throw IllegalArgumentException()
    }
  }

  override fun put(int: Int, byteOrder: ByteOrder) {
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> byteBuf.writeInt(int)
      ByteOrder.LITTLE_ENDIAN -> byteBuf.writeIntLE(int)
      else -> throw IllegalArgumentException()
    }
  }

  override fun put(long: Long, byteOrder: ByteOrder) {
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> byteBuf.writeLong(long)
      ByteOrder.LITTLE_ENDIAN -> byteBuf.writeLongLE(long)
      else -> throw IllegalArgumentException()
    }
  }

  override fun put(float: Float, byteOrder: ByteOrder) {
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> byteBuf.writeFloat(float)
      ByteOrder.LITTLE_ENDIAN -> byteBuf.writeFloatLE(float)
      else -> throw IllegalArgumentException()
    }
  }

  override fun put(double: Double, byteOrder: ByteOrder) {
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> byteBuf.writeDouble(double)
      ByteOrder.LITTLE_ENDIAN -> byteBuf.writeDoubleLE(double)
      else -> throw IllegalArgumentException()
    }
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

  override fun readBufferArray(): Array<out java.nio.ByteBuffer> {
    val nioBuffers = byteBuf.nioBuffers()
    nioReadPosition = nioBuffers.sumOf { it.position() }
    nioReadBuffersNum = nioBuffers.size
    return nioBuffers
  }

  override fun finishRead(buffers: Iterator<java.nio.ByteBuffer>) {
    var readPositionResult = 0
    repeat(nioReadBuffersNum) {
      readPositionResult += buffers.next().position()
    }
    nioReadBuffersNum = 0
    byteBuf.readerIndex(byteBuf.readerIndex() + readPositionResult - nioReadPosition)
  }

  override fun writeBufferArray(): Array<out java.nio.ByteBuffer> {
    val nioBuffers = byteBuf.nioBuffers(byteBuf.writerIndex(), byteBuf.writableBytes())
    nioWritePosition = nioBuffers.sumOf { it.position() }
    nioWriteBuffersNum = nioBuffers.size
    return nioBuffers
  }

  override fun finishWrite(buffers: Iterator<java.nio.ByteBuffer>) {
    var writePositionResult = 0
    repeat(nioWriteBuffersNum) {
      writePositionResult += buffers.next().position()
    }
    nioWriteBuffersNum = 0
    byteBuf.writerIndex(byteBuf.writerIndex() + writePositionResult - nioWritePosition)
  }

  override fun toString(): String {
    return "Nettyjava.nio.ByteBuffer(byteBuf=$byteBuf)"
  }
}