package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.MultipleByteBuffer
import java.nio.ByteOrder

@Suppress("MemberVisibilityCanBePrivate")
open class ListByteBuffer(
  final override val bufferIterable: MutableList<ByteBuffer> = ArrayList(),
) : MultipleByteBuffer {
  var readOperator = bufferIterable.firstOrNull()
  var writeOperator = bufferIterable.firstOrNull()
  var readArrayPosition: Int = if (readOperator == null) -1 else 0
  var writeArrayPosition: Int = if (writeOperator == null) -1 else 0

  private var buffersArrayCache: Array<out ByteBuffer>? = null
  override val buffers: Array<out ByteBuffer>
    get() {
      if (buffersArrayCache == null) {
        buffersArrayCache = bufferIterable.toTypedArray()
      }
      return buffersArrayCache!!
    }

  final override var capacity: Int = bufferIterable.sumOf { it.capacity }
    private set

  override var writePosition: Int = bufferIterable.sumOf { it.writePosition }
  override var readPosition: Int = bufferIterable.sumOf { it.readPosition }

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

  override val bufferSize: Int get() = bufferIterable.size

  override fun clear() {
    super.clear()
    readPosition = 0
    writePosition = 0
    readArrayPosition = 0
    writeArrayPosition = 0
  }

  override fun reset() {
    super.reset()
    readPosition = 0
    writePosition = 0
    readArrayPosition = 0
    writeArrayPosition = 0
  }

  override fun skip(n: Int): Int {
    return when {
      n == 0 -> 0
      n > 0 -> {
        var skip = 0
        while (skip != n) {
          skip += readOperator?.skip(n - skip) ?: 0
          if (readArrayPosition < bufferIterable.size) {
            readOperator = bufferIterable[readArrayPosition++]
          } else {
            break
          }
        }
        skip
      }
      else -> {
        var fallback = 0
        while (fallback != n) {
          fallback += readOperator?.skip(n - fallback) ?: 0
          if (readArrayPosition > 0) {
            readOperator = bufferIterable[--readArrayPosition]
          } else {
            break
          }
        }
        fallback
      }
    }
  }

  override fun readBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun finishRead(buffer: java.nio.ByteBuffer) = throw UnsupportedOperationException()

  override fun writeBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun finishWrite(buffer: java.nio.ByteBuffer) = throw UnsupportedOperationException()

  override fun readBufferArray(): Array<out java.nio.ByteBuffer> {
    val iterator = bufferIterable.iterator()
    return Array(array.size) {
      iterator.next().readBuffer()
    }
  }

  override fun finishRead(buffers: Array<out java.nio.ByteBuffer>): Long {
    val iterator = this.bufferIterable.iterator()
    var readed = 0L
    buffers.forEach { buffer ->
      readed += iterator.next().finishRead(buffer)
    }
    return readed
  }

  override fun writeBufferArray(): Array<out java.nio.ByteBuffer> {
    val iterator = bufferIterable.iterator()
    return Array(array.size) {
      iterator.next().writeBuffer()
    }
  }

  override fun finishWrite(buffers: Array<out java.nio.ByteBuffer>): Long {
    val iterator = bufferIterable.iterator()
    var written = 0L
    buffers.forEach { buffer ->
      written += iterator.next().finishWrite(buffer)
    }
    return written
  }

  override fun append(buffer: ByteBuffer) {
    if (buffer is MultipleByteBuffer) {
      bufferIterable.addAll(buffer.bufferIterable)
    } else {
      bufferIterable.add(buffer)
    }
    buffersArrayCache = null
    capacity += buffer.capacity
    readPosition += buffer.readPosition
    writePosition += buffer.writePosition
  }

  override fun resize(newSize: Int): Boolean = throw UnsupportedOperationException()

  private fun updateRead() {
    while (readArrayPosition < bufferIterable.size && readOperator?.isReadable != true) {
      readOperator = bufferIterable[readArrayPosition++]
    }
  }

  private fun updateWrite() {
    while (writeArrayPosition < bufferIterable.size && writeOperator?.isWriteable != true) {
      writeOperator = bufferIterable[writeArrayPosition++]
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