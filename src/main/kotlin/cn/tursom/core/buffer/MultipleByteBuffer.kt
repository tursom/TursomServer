package cn.tursom.buffer

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.ListByteBuffer
import cn.tursom.core.forEachIndex
import cn.tursom.core.toBytes
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.min

@Suppress("unused")
interface MultipleByteBuffer : List<ByteBuffer>, Closeable, ByteBuffer {
  val buffers: Array<out ByteBuffer> get() = toTypedArray()

  /**
   * 使用读 buffer，ByteBuffer 实现类有义务维护指针正常推进
   */
  fun <T> readBuffers(block: (List<java.nio.ByteBuffer>) -> T): T {
    val buffer = readBuffers()
    return try {
      block(buffer)
    } finally {
      finishRead(buffer)
    }
  }

  /**
   * 使用写 buffer，ByteBuffer 实现类有义务维护指针正常推进
   */
  fun <T> writeBuffers(block: (List<java.nio.ByteBuffer>) -> T): T {
    val buffer = writeBuffers()
    return try {
      block(buffer)
    } finally {
      finishWrite(buffer)
    }
  }

  fun readBuffers(): List<java.nio.ByteBuffer> {
    val bufferList = ArrayList<java.nio.ByteBuffer>()
    buffers.forEach {
      if (it is MultipleByteBuffer) {
        it.forEach {
          bufferList.add(it.readBuffer())
        }
      } else {
        bufferList.add(it.readBuffer())
      }
    }
    return bufferList
  }

  fun writeBuffers(): List<java.nio.ByteBuffer> {
    val bufferList = ArrayList<java.nio.ByteBuffer>()
    buffers.forEach {
      if (it is MultipleByteBuffer) {
        it.forEach {
          bufferList.add(it.writeBuffer())
        }
      } else {
        bufferList.add(it.writeBuffer())
      }
    }
    return bufferList
  }

  fun finishRead(buffers: List<java.nio.ByteBuffer>) {
    var index = 0
    forEach {
      if (it is MultipleByteBuffer) {
        it.forEach {
          it.finishRead(buffers[index])
          index++
        }
      } else {
        it.finishRead(buffers[index])
        index++
      }
    }
  }

  fun finishWrite(buffers: List<java.nio.ByteBuffer>) {
    var index = 0
    forEach {
      if (it is MultipleByteBuffer) {
        it.forEach {
          it.finishWrite(buffers[index])
          index++
        }
      } else {
        it.finishWrite(buffers[index])
        index++
      }
    }
  }

  override fun close() = forEach(ByteBuffer::close)
  override fun slice(offset: Int, size: Int): MultipleByteBuffer = ListByteBuffer(subList(offset, offset + size))
  override fun fill(byte: Byte) = forEach { it.fill(byte) }
  override fun clear() = forEach(ByteBuffer::clear)
  override fun reset() = forEach(ByteBuffer::reset)


  override val resized: Boolean get() = false
  override val hasArray: Boolean get() = false
  override val array: ByteArray
    get() = throw UnsupportedOperationException()
  override val arrayOffset: Int get() = 0
  override val capacity: Int
    get() {
      var capacity = 0
      forEach {
        capacity += it.capacity
      }
      return capacity
    }

  override fun readBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun writeBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer =
    throw UnsupportedOperationException()

  override fun resize(newSize: Int): Boolean = false

  override fun get(): Byte
  override fun getChar(): Char = cn.tursom.core.toChar(::get)
  override fun getShort(): Short = cn.tursom.core.toShort(::get)
  override fun getInt(): Int = cn.tursom.core.toInt(::get)
  override fun getLong(): Long = cn.tursom.core.toLong(::get)
  override fun getFloat(): Float = cn.tursom.core.toFloat(::get)
  override fun getDouble(): Double = cn.tursom.core.toDouble(::get)
  override fun getBytes(size: Int): ByteArray {
    val buffer = ByteArray(size)
    buffer.indices.forEach {
      buffer[it] = get()
    }
    return buffer
  }

  override fun writeTo(buffer: ByteArray, bufferOffset: Int, size: Int): Int {
    var write = 0
    try {
      repeat(size) {
        buffer[bufferOffset + it] = get()
        write++
      }
    } catch (e: Exception) {
    }
    return write
  }

  override fun writeTo(os: OutputStream): Int {
    var write = 0
    try {
      while (true) {
        os.write(get().toInt())
        write++
      }
    } catch (e: Exception) {
    }
    return write
  }

  override fun writeTo(buffer: ByteBuffer): Int {
    var write = 0
    try {
      while (true) {
        buffer.put(get().toInt())
        write++
      }
    } catch (e: Exception) {
    }
    return write
  }

  override fun put(byte: Byte): Unit
  override fun put(char: Char) = char.toBytes { put(it) }
  override fun put(short: Short) = short.toBytes { put(it) }
  override fun put(int: Int) = int.toBytes { put(it) }
  override fun put(long: Long) = long.toBytes { put(it) }
  override fun put(float: Float) = float.toBytes { put(it) }
  override fun put(double: Double) = double.toBytes { put(it) }
  override fun put(byteArray: ByteArray, offset: Int, len: Int): Int {
    var write = 0
    byteArray.forEachIndex(offset, offset + len) {
      put(it)
      write++
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

  override fun split(maxSize: Int): Array<out ByteBuffer> = throw UnsupportedOperationException()
  override fun readAllSize(): Int = throw UnsupportedOperationException()
}