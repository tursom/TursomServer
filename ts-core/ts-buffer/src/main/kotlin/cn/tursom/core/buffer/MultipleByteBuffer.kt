package cn.tursom.core.buffer

import cn.tursom.core.buffer.impl.ListByteBuffer
import cn.tursom.core.forEachIndex
import cn.tursom.core.toBytes
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteOrder

@Suppress("unused")
interface MultipleByteBuffer : Closeable, ByteBuffer, NioBuffers.Sequences {
  val buffers: List<ByteBuffer> get() = listOf(this)
  val buffersArray: Array<out ByteBuffer> get() = arrayOf(this)

  fun append(buffer: ByteBuffer)

  /**
   * 使用读 buffer，ByteBuffer 实现类有义务维护指针正常推进
   */
  fun <T> readBuffers(block: (Sequence<java.nio.ByteBuffer>) -> T): T {
    val buffer = readBufferSequence()
    return try {
      block(buffer)
    } finally {
      finishRead(buffer)
    }
  }

  /**
   * 使用写 buffer，ByteBuffer 实现类有义务维护指针正常推进
   */
  fun <T> writeBuffers(block: (Sequence<java.nio.ByteBuffer>) -> T): T {
    val buffer = writeBufferSequence()
    return try {
      block(buffer)
    } finally {
      finishWrite(buffer)
    }
  }

  override fun readBufferSequence(): Sequence<java.nio.ByteBuffer> = sequence {
    buffers.forEach { subBuf ->
      val sequences = subBuf.getExtension(NioBuffers.Sequences)
      if (sequences != null) {
        yieldAll(sequences.readBufferSequence())
      } else {
        yield(subBuf.readBuffer())
      }
    }
  }

  override fun writeBufferSequence(): Sequence<java.nio.ByteBuffer> = sequence {
    buffers.forEach { subBuf ->
      val sequences = subBuf.getExtension(NioBuffers.Sequences)
      if (sequences != null) {
        yieldAll(sequences.writeBufferSequence())
      } else {
        yield(subBuf.writeBuffer())
      }
    }
  }

  fun finishRead(buffers: Sequence<java.nio.ByteBuffer>) = finishRead(buffers.iterator())
  override fun finishRead(buffers: Iterator<java.nio.ByteBuffer>) {
    this.buffers.forEach { subBuf ->
      val sequences = subBuf.getExtension(NioBuffers.Sequences)
      if (sequences != null) {
        sequences.finishRead(buffers)
      } else {
        subBuf.finishRead(buffers.next())
      }
    }
  }

  fun finishWrite(buffers: Sequence<java.nio.ByteBuffer>) = finishWrite(buffers.iterator())
  override fun finishWrite(buffers: Iterator<java.nio.ByteBuffer>) {
    this.buffers.forEach { subBuf ->
      val sequences = subBuf.getExtension(NioBuffers.Sequences)
      if (sequences != null) {
        sequences.finishWrite(buffers)
      } else {
        subBuf.finishWrite(buffers.next())
      }
    }
  }

  override fun close() = buffers.forEach(ByteBuffer::close)
  override fun slice(position: Int, size: Int): MultipleByteBuffer {
    return ListByteBuffer(ArrayList(buffers.subList(position, position + size)))
  }

  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer =
    throw UnsupportedOperationException()

  override fun fill(byte: Byte) = buffers.forEach { it.fill(byte) }
  override fun clear() = buffers.forEach { it.clear() }
  override fun reset() = buffers.forEach { it.reset() }


  override val resized: Boolean get() = false
  override val hasArray: Boolean get() = false
  override val array: ByteArray get() = throw UnsupportedOperationException()
  override val arrayOffset: Int get() = 0
  override val capacity: Int get() = buffers.sumOf { it.capacity }
  override val isReadable: Boolean get() = buffers.any { it.isReadable }
  override val isWriteable: Boolean get() = buffers.any { it.isWriteable }
  override val readable: Int get() = buffers.sumOf { it.readable }
  override val writeable: Int get() = buffers.sumOf { it.writeable }

  override fun readBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun writeBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()

  override fun resize(newSize: Int): Boolean = false

  override fun get(): Byte

  override fun getChar(byteOrder: ByteOrder): Char = cn.tursom.core.toChar(byteOrder) { get() }
  override fun getShort(byteOrder: ByteOrder): Short = cn.tursom.core.toShort(byteOrder) { get() }
  override fun getInt(byteOrder: ByteOrder): Int = cn.tursom.core.toInt(byteOrder) { get() }
  override fun getLong(byteOrder: ByteOrder): Long = cn.tursom.core.toLong(byteOrder) { get() }
  override fun getFloat(byteOrder: ByteOrder): Float = cn.tursom.core.toFloat(byteOrder) { get() }
  override fun getDouble(byteOrder: ByteOrder): Double = cn.tursom.core.toDouble(byteOrder) { get() }

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

  override fun writeTo(os: OutputStream, buffer: ByteArray?): Int {
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

  override fun split(maxSize: Int): List<ByteBuffer> = throw UnsupportedOperationException()
  override fun readAllSize(): Int = throw UnsupportedOperationException()
}