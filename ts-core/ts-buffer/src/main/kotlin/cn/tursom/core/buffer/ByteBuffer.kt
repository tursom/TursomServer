package cn.tursom.core.buffer

import cn.tursom.core.Utils.bufferThreadLocal
import cn.tursom.core.forEachIndex
import cn.tursom.core.reverseBytes
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteOrder
import kotlin.math.min

/**
 * 针对其他库的字节缓冲的封装
 */
@Suppress("unused")
interface ByteBuffer : Closeable {
  fun <T> getExtension(key: ByteBufferExtensionKey<T>): T? = key.get(this)

  /**
   * 使用读 buffer，ByteBuffer 实现类有义务维护指针正常推进
   */
  fun <T> readBuffer(block: (java.nio.ByteBuffer) -> T): T {
    val buffer = readBuffer()
    return try {
      block(buffer)
    } finally {
      finishRead(buffer)
    }
  }

  /**
   * 使用写 buffer，ByteBuffer 实现类有义务维护指针正常推进
   */
  fun <T> writeBuffer(block: (java.nio.ByteBuffer) -> T): T {
    val buffer = writeBuffer()
    return try {
      block(buffer)
    } finally {
      finishWrite(buffer)
    }
  }

  val readable: Int get() = writePosition - readPosition
  val writeable: Int get() = capacity - writePosition

  val isReadable: Boolean get() = readable != 0
  val isWriteable: Boolean get() = writeable != 0

  val hasArray: Boolean
  val array: ByteArray

  val capacity: Int
  val arrayOffset: Int
  var writePosition: Int
  var readPosition: Int

  val closed: Boolean get() = false
  val resized: Boolean

  override fun close() {}
  fun closeChild(child: ByteBuffer) {}

  fun readBuffer(): java.nio.ByteBuffer
  fun finishRead(buffer: java.nio.ByteBuffer) {
    readPosition += buffer.position()
  }

  fun writeBuffer(): java.nio.ByteBuffer
  fun finishWrite(buffer: java.nio.ByteBuffer) {
    writePosition += buffer.position()
  }

  fun reset()
  fun slice(position: Int, size: Int): ByteBuffer = slice(position, size, 0, 0)
  fun slice(position: Int, size: Int, readPosition: Int = 0, writePosition: Int = 0): ByteBuffer

  /**
   * @return 底层 nio buffer 是否已更新
   */
  fun resize(newSize: Int): Boolean

  val writeOffset: Int get() = arrayOffset + writePosition
  val readOffset: Int get() = arrayOffset + readPosition

  fun clear() {
    readPosition = 0
    writePosition = 0
  }

  fun skip(n: Int) {
    readPosition += n
  }

  fun get(): Byte = read { it.get() }

  fun getChar(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Char = read { buf ->
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> buf.char
      ByteOrder.LITTLE_ENDIAN -> buf.char.reverseBytes()
      else -> throw IllegalArgumentException()
    }
  }

  fun getShort(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Short = read { buf ->
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> buf.short
      ByteOrder.LITTLE_ENDIAN -> buf.short.reverseBytes()
      else -> throw IllegalArgumentException()
    }
  }

  fun getInt(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Int = read { buf ->
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> buf.int
      ByteOrder.LITTLE_ENDIAN -> buf.int.reverseBytes()
      else -> throw IllegalArgumentException()
    }
  }

  fun getLong(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Long = read { buf ->
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> buf.long
      ByteOrder.LITTLE_ENDIAN -> buf.long.reverseBytes()
      else -> throw IllegalArgumentException()
    }
  }

  fun getFloat(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Float = read { buf ->
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> buf.float
      ByteOrder.LITTLE_ENDIAN -> buf.float.reverseBytes()
      else -> throw IllegalArgumentException()
    }
  }

  fun getDouble(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Double = read { buf ->
    when (byteOrder) {
      ByteOrder.BIG_ENDIAN -> buf.double
      ByteOrder.LITTLE_ENDIAN -> buf.double.reverseBytes()
      else -> throw IllegalArgumentException()
    }
  }

  fun getBytes(size: Int = readable): ByteArray = read {
    if (it.limit() - it.position() < size) {
      throw IndexOutOfBoundsException()
    }
    val bytes = ByteArray(size)
    it.get(bytes)
    bytes
  }

  fun getString(size: Int = readable): String = String(getBytes(size))

  fun toString(size: Int): String {
    val bytes = getBytes(size)
    // 将测试的字节返还回来
    readPosition -= bytes.size
    return String(bytes)
  }

  fun writeTo(buffer: ByteArray, bufferOffset: Int = 0, size: Int = min(readable, buffer.size)): Int {
    val readSize = min(readable, size)
    if (hasArray) {
      array.copyInto(buffer, bufferOffset, readOffset, readOffset + readSize)
      readPosition += readOffset
      reset()
    } else {
      read {
        it.put(buffer, bufferOffset, readSize)
      }
    }
    return readSize
  }

  fun writeTo(os: OutputStream, buffer: ByteArray? = null): Int {
    val size = readable
    if (hasArray) {
      os.write(array, readOffset, size)
      readPosition += size
      reset()
    } else {
      @Suppress("NAME_SHADOWING")
      val buffer = buffer ?: bufferThreadLocal.get()
      read {
        while (it.remaining() > 0) {
          it.put(buffer)
          os.write(buffer)
        }
      }
    }
    return size
  }

  fun writeTo(buffer: ByteBuffer): Int {
    val size = min(readable, buffer.writeable)
    if (hasArray) {
      buffer.put(array, readOffset, size)
      readPosition += size
      reset()
    } else {
      read { read ->
        buffer.write { write -> write.put(read) }
      }
    }
    return size
  }

  fun toByteArray() = getBytes()


  /*
   * 数据写入方法
   */

  fun put(byte: Byte): Unit = write { it.put(byte) }

  fun put(char: Char, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = write { buf ->
    buf.order(byteOrder)
    buf.putChar(char)
  }

  fun put(short: Short, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = write { buf ->
    buf.order(byteOrder)
    buf.putShort(short)
  }

  fun put(int: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = write { buf ->
    buf.order(byteOrder)
    buf.putInt(int)
  }

  fun put(long: Long, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = write { buf ->
    buf.order(byteOrder)
    buf.putLong(long)
  }

  fun put(float: Float, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = write { buf ->
    buf.order(byteOrder)
    buf.putFloat(float)
  }

  fun put(double: Double, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = write { buf ->
    buf.order(byteOrder)
    buf.putDouble(double)
  }

  fun put(str: String): Int = put(str.toByteArray())
  fun put(buffer: ByteBuffer): Int = buffer.writeTo(this)
  fun put(byteArray: ByteArray, offset: Int = 0, len: Int = byteArray.size - offset): Int {
    return if (hasArray) {
      byteArray.copyInto(array, writeOffset, offset, offset + len)
      writePosition += len
      len
    } else {
      write {
        val position = it.position()
        it.put(byteArray, offset, len)
        it.position() - position
      }
    }
  }

  fun put(
    array: CharArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) {
    array.forEachIndex(index, index + size - 1) { put(it, byteOrder) }
  }

  fun put(
    array: ShortArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) {
    array.forEachIndex(index, index + size - 1) { put(it, byteOrder) }
  }

  fun put(
    array: IntArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) {
    array.forEachIndex(index, index + size - 1) { put(it, byteOrder) }
  }

  fun put(
    array: LongArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) {
    array.forEachIndex(index, index + size - 1) { put(it, byteOrder) }
  }

  fun put(
    array: FloatArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) {
    array.forEachIndex(index, index + size - 1) { put(it, byteOrder) }
  }

  fun put(
    array: DoubleArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) {
    array.forEachIndex(index, index + size - 1) { put(it, byteOrder) }
  }

  fun put(inputStream: InputStream): Int {
    return if (hasArray) {
      val read = inputStream.read(array, writeOffset, writeable)
      if (read < 0) throw IOException("stream closed")
      writePosition += read
      read
    } else {
      val buffer = bufferThreadLocal.get()
      val read = inputStream.read(buffer)
      put(buffer, 0, read)
    }
  }

  fun put(inputStream: InputStream, size: Int): Int {
    assert(size <= writeable)
    return if (hasArray) {
      val read = inputStream.read(array, writeOffset, size)
      if (read > 0) {
        writePosition += read
      }
      read
    } else {
      val buffer = ByteArray(size)
      val read = inputStream.read(buffer)
      if (read > 0) {
        put(buffer, 0, read)
      }
      read
    }
  }

  fun putByte(byte: Byte): Unit = put(byte)
  fun putChar(char: Char, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = put(char, byteOrder)
  fun putShort(short: Short, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = put(short, byteOrder)
  fun putInt(int: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = put(int, byteOrder)
  fun putLong(long: Long, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = put(long, byteOrder)
  fun putFloat(float: Float, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = put(float, byteOrder)
  fun putDouble(double: Double, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Unit = put(double, byteOrder)

  fun putString(str: String): Int = put(str)
  fun putBuffer(buffer: ByteBuffer): Int = put(buffer)
  fun putBytes(byteArray: ByteArray, startIndex: Int = 0, endIndex: Int = byteArray.size - startIndex) =
    put(byteArray, startIndex, endIndex)

  fun putChars(
    array: CharArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) = put(array, index, size, byteOrder)

  fun putShorts(
    array: ShortArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) = put(array, index, size, byteOrder)

  fun putInts(
    array: IntArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) = put(array, index, size, byteOrder)

  fun putLongs(
    array: LongArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) = put(array, index, size, byteOrder)

  fun putFloats(
    array: FloatArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) = put(array, index, size, byteOrder)

  fun putDoubles(
    array: DoubleArray,
    index: Int = 0,
    size: Int = array.size - index,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
  ) = put(array, index, size, byteOrder)

  fun fill(byte: Byte) {
    readPosition = 0
    writePosition = 0
    write {
      while (it.remaining() != 0) {
        it.put(byte)
      }
    }
    writePosition = 0
  }

  fun split(maxSize: Int): List<ByteBuffer> {
    val size = (((capacity - 1) / maxSize) + 1).and(0x7fff_ffff)
    return Array(size) {
      if (it != size - 1) {
        slice(it * maxSize, maxSize)
      } else {
        slice(it * maxSize, capacity - it * maxSize)
      }
    }.asList()
  }

  fun readAllSize(): Int {
    val size = readable
    readPosition += size
    return size
  }
}