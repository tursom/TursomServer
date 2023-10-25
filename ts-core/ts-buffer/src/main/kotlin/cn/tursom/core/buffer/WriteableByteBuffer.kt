package cn.tursom.core.buffer

import cn.tursom.core.util.Utils
import cn.tursom.core.util.forEachIndex
import java.io.IOException
import java.io.InputStream
import java.nio.ByteOrder

interface WriteableByteBuffer : BasicByteBuffer {
  fun writeBuffer(): java.nio.ByteBuffer
  fun finishWrite(buffer: java.nio.ByteBuffer): Int

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
  fun put(buffer: ReadableByteBuffer): Int = buffer.writeTo(this)
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
      val buffer = Utils.bufferThreadLocal.get()
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
  fun putBuffer(buffer: ReadableByteBuffer): Int = put(buffer)
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
}
