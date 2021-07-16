package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.MultipleByteBuffer
import java.nio.ByteOrder

@Suppress("MemberVisibilityCanBePrivate")
open class ListByteBuffer(
  final override val buffers: MutableList<ByteBuffer> = ArrayList(),
) : MultipleByteBuffer {
  var readArrayPosition: Int = 0
  var writeArrayPosition: Int = 0
  var readOperator = buffers.firstOrNull()
  var writeOperator = buffers.firstOrNull()

  private var buffersArrayCache: Array<out ByteBuffer>? = null
  override val buffersArray: Array<out ByteBuffer>
    get() {
      if (buffersArrayCache == null) {
        buffersArrayCache = buffers.toTypedArray()
      }
      return buffersArrayCache!!
    }

  override val hasArray: Boolean get() = false
  override val array: ByteArray get() = throw UnsupportedOperationException()
  override val capacity: Int get() = buffers.sumOf { it.capacity }

  override var writePosition: Int = buffers.sumOf { it.writePosition }
  override var readPosition: Int = buffers.sumOf { it.readPosition }

  override val resized: Boolean get() = false

  override val isReadable: Boolean
    get() {
      updateRead()
      return readOperator?.isReadable ?: false
    }
  override val isWriteable: Boolean
    get() {
      updateWrite()
      return writeOperator?.isWriteable ?: false
    }

  override fun readBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun writeBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()

  override fun append(buffer: ByteBuffer) {
    val bufReadPosition = buffer.readPosition
    val bufWritePosition = buffer.writePosition
    buffers.add(buffer)
    buffersArrayCache = null
    readPosition += bufReadPosition
    writePosition += bufWritePosition
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer =
    throw UnsupportedOperationException()

  override fun resize(newSize: Int): Boolean = throw UnsupportedOperationException()

  private fun updateRead() {
    while (readArrayPosition < buffers.size && readOperator?.isReadable != true) {
      readOperator = buffers[readArrayPosition++]
    }
  }

  private fun updateWrite() {
    while (writeArrayPosition < buffers.size && writeOperator?.isWriteable == true) {
      writeOperator = buffers[writeArrayPosition++]
    }
  }

  override fun get(): Byte {
    updateRead()
    val get = readOperator!!.get()
    readPosition++
    return get
  }

  override fun getChar(byteOrder: ByteOrder): Char {
    updateRead()
    return if (readOperator!!.readable >= 2) {
      val char = readOperator!!.getChar(byteOrder)
      readPosition += 2
      char
    } else {
      super.getChar(byteOrder)
    }
  }

  override fun getShort(byteOrder: ByteOrder): Short {
    updateRead()
    return if (readOperator!!.readable >= 2) {
      val short = readOperator!!.getShort(byteOrder)
      readPosition += 2
      short
    } else {
      super.getShort(byteOrder)
    }
  }

  override fun getInt(byteOrder: ByteOrder): Int {
    updateRead()
    return if (readOperator!!.readable >= 4) {
      val int = readOperator!!.getInt(byteOrder)
      readPosition += 4
      int
    } else {
      super.getInt(byteOrder)
    }
  }

  override fun getLong(byteOrder: ByteOrder): Long {
    updateRead()
    return if (readOperator!!.readable >= 8) {
      val long = readOperator!!.getLong(byteOrder)
      readPosition += 8
      long
    } else {
      super.getLong(byteOrder)
    }
  }

  override fun getFloat(byteOrder: ByteOrder): Float {
    updateRead()
    return if (readOperator!!.readable >= 4) {
      val float = readOperator!!.getFloat(byteOrder)
      readPosition += 4
      float
    } else {
      super.getFloat(byteOrder)
    }
  }

  override fun getDouble(byteOrder: ByteOrder): Double {
    updateRead()
    return if (readOperator!!.readable >= 8) {
      val double = readOperator!!.getDouble(byteOrder)
      readPosition += 8
      double
    } else {
      super.getDouble(byteOrder)
    }
  }

  override fun getBytes(size: Int): ByteArray {
    return if (readOperator!!.readable >= size) {
      val bytes = readOperator!!.getBytes(size)
      readPosition += size
      bytes
    } else {
      super.getBytes(size)
    }
  }

  override fun put(byte: Byte) {
    updateWrite()
    writeOperator!!.put(byte)
    writePosition++
  }

  override fun put(char: Char, byteOrder: ByteOrder) {
    updateWrite()
    if (writeOperator!!.writeable > 2) {
      writeOperator!!.put(char, byteOrder)
      writePosition += 2
    } else {
      super.put(char, byteOrder)
    }
  }

  override fun put(short: Short, byteOrder: ByteOrder) {
    updateWrite()
    if (writeOperator!!.writeable > 2) {
      writeOperator!!.put(short, byteOrder)
      writePosition += 2
    } else {
      super.put(short, byteOrder)
    }
  }

  override fun put(int: Int, byteOrder: ByteOrder) {
    updateWrite()
    if (writeOperator!!.writeable > 4) {
      writeOperator!!.put(int, byteOrder)
      writePosition += 4
    } else {
      super.put(int, byteOrder)
    }
  }

  override fun put(long: Long, byteOrder: ByteOrder) {
    updateWrite()
    if (writeOperator!!.writeable > 8) {
      writeOperator!!.put(long, byteOrder)
      writePosition += 8
    } else {
      super.put(long, byteOrder)
    }
  }

  override fun put(float: Float, byteOrder: ByteOrder) {
    updateWrite()
    if (writeOperator!!.writeable > 4) {
      writeOperator!!.put(float, byteOrder)
      writePosition += 4
    } else {
      super.put(float, byteOrder)
    }
  }

  override fun put(double: Double, byteOrder: ByteOrder) {
    updateWrite()
    if (writeOperator!!.writeable > 8) {
      writeOperator!!.put(double, byteOrder)
      writePosition += 8
    } else {
      super.put(double, byteOrder)
    }
  }
}