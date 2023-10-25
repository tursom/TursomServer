package cn.tursom.core.buffer

import cn.tursom.core.buffer.NioBuffers.readNioBuffers
import cn.tursom.core.buffer.NioBuffers.writeNioBuffers
import cn.tursom.core.util.*
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteOrder
import kotlin.math.min

@Suppress("unused")
interface MultipleByteBuffer : Closeable, ByteBuffer, NioBuffers.Arrays {
  val buffers: Array<out ByteBuffer>
  val bufferIterable: Iterable<ByteBuffer>
  val bufferSize: Int get() = buffers.size

  /**
   * append and give ownership to this MultipleByteBuffer
   */
  fun append(buffer: ByteBuffer)

  override fun close() = bufferIterable.forEach(ByteBuffer::close)
  override fun slice(position: Int, size: Int): MultipleByteBuffer =
    throw UnsupportedOperationException()

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer =
    throw UnsupportedOperationException()

  override fun fill(byte: Byte) = bufferIterable.forEach { it.fill(byte) }
  override fun clear() = bufferIterable.forEach { it.clear() }
  override fun reset() = bufferIterable.forEach { it.reset() }


  override val resized: Boolean get() = false
  override val hasArray: Boolean get() = false
  override val array: ByteArray get() = throw UnsupportedOperationException()
  override val arrayOffset: Int get() = 0

  override fun readBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun finishRead(buffer: java.nio.ByteBuffer) = throw UnsupportedOperationException()
  override fun writeBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun finishWrite(buffer: java.nio.ByteBuffer) = throw UnsupportedOperationException()

  override fun resize(newSize: Int): Boolean = false

  override fun get(): Byte

  override fun getChar(byteOrder: ByteOrder): Char = toChar(byteOrder) { get() }
  override fun getShort(byteOrder: ByteOrder): Short = toShort(byteOrder) { get() }
  override fun getInt(byteOrder: ByteOrder): Int = toInt(byteOrder) { get() }
  override fun getLong(byteOrder: ByteOrder): Long = toLong(byteOrder) { get() }
  override fun getFloat(byteOrder: ByteOrder): Float = toFloat(byteOrder) { get() }
  override fun getDouble(byteOrder: ByteOrder): Double = toDouble(byteOrder) { get() }

  override fun getBytes(size: Int): ByteArray {
    if (size == 0) {
      return ByteArray(0)
    }
    val buffer = ByteArray(size)
    writeTo(buffer, 0, size)
    return buffer
  }

  override fun writeTo(buffer: ByteArray, bufferOffset: Int, size: Int): Int {
    if (size == 0) {
      return 0
    }
    var writePosition = bufferOffset
    readNioBuffers { buffers ->
      buffers.forEach { buf ->
        val limit = min(buf.remaining(), size - writePosition)
        if (limit == 0) {
          return@forEach
        }
        buf.get(buffer, writePosition, limit)
        writePosition += limit
        if (writePosition == size) {
          return@readNioBuffers
        }
      }
    }
    return writePosition - bufferOffset
  }

  override fun writeTo(os: OutputStream, buffer: ByteArray?): Int {
    var write = 0

    @Suppress("NAME_SHADOWING")
    val buffer = buffer ?: Utils.bufferThreadLocal.get()
    do {
      val written = writeTo(buffer)
      write += written
      os.write(buffer, 0, written)
    } while (written != 0)
    return write
  }

  override fun writeTo(buffer: WriteableByteBuffer): Int {
    var write = 0
    val buf = Utils.bufferThreadLocal.get()
    do {
      val written = writeTo(buf, 0, min(buffer.writeable, min(readable, buf.size)))
      if (written == 0) {
        break
      }
      write += written
      buffer.putBytes(buf, 0, written)
      write++
    } while (true)
    return write
  }

  override fun put(byte: Byte)
  override fun put(char: Char, byteOrder: ByteOrder) {
    char.toBytes { put(it) }
  }

  override fun put(short: Short, byteOrder: ByteOrder) {
    short.toBytes { put(it) }
  }

  override fun put(int: Int, byteOrder: ByteOrder) {
    int.toBytes { put(it) }
  }

  override fun put(long: Long, byteOrder: ByteOrder) {
    long.toBytes { put(it) }
  }

  override fun put(float: Float, byteOrder: ByteOrder) {
    float.toBytes { put(it) }
  }

  override fun put(double: Double, byteOrder: ByteOrder) {
    double.toBytes { put(it) }
  }

  override fun put(byteArray: ByteArray, offset: Int, len: Int): Int {
    var write = 0
    writeNioBuffers { buffers ->
      buffers.forEach {
        if (it.remaining() == 0) {
          return@forEach
        }
        val limit = min(it.remaining(), len - write)
        it.put(byteArray, offset + write, limit)
        write += limit
        if (len == write) {
          return@writeNioBuffers
        }
      }
    }
    return write
  }

  override fun put(inputStream: InputStream, size: Int): Int {
    var read = 0
    try {
      put(inputStream.read().toByte())
      read++
    } catch (e: Exception) {
    }
    return read
  }

  override fun split(maxSize: Int): Array<ByteBuffer> = throw UnsupportedOperationException()
  override fun readAllSize(): Int = throw UnsupportedOperationException()
}