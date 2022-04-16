package cn.tursom.core.buffer

import cn.tursom.core.Utils
import cn.tursom.core.reverseBytes
import java.io.OutputStream
import java.nio.ByteOrder
import kotlin.math.min

interface ReadableByteBuffer : BasicByteBuffer {
  fun readBuffer(): java.nio.ByteBuffer
  fun finishRead(buffer: java.nio.ByteBuffer): Int

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

  fun skip(n: Int): Int {
    readPosition += n
    return n
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
      val buffer = buffer ?: Utils.bufferThreadLocal.get()
      read {
        while (it.remaining() > 0) {
          val min = min(it.remaining(), buffer.size)
          it.get(buffer, 0, min)
          os.write(buffer, 0, min)
        }
      }
    }
    return size
  }

  fun writeTo(buffer: WriteableByteBuffer): Int {
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
}
