package cn.tursom.core.buffer.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.MultipleByteBuffer
import cn.tursom.core.buffer.NioBuffers.finishRead
import cn.tursom.core.buffer.NioBuffers.finishWrite
import cn.tursom.core.buffer.NioBuffers.getReadNioBufferList
import cn.tursom.core.buffer.NioBuffers.getWriteNioBufferList
import cn.tursom.core.uncheckedCast

class ArrayByteBuffer(
  vararg buffers: ByteBuffer,
) : MultipleByteBuffer {
  constructor(
    buffer: Array<out ByteBuffer>,
    offset: Int = 0,
    length: Int = buffer.size - offset,
  ) : this(buffers = Array(length) { buffer[offset + it] })

  override val bufferIterable: List<ByteBuffer> get() = buffers.asList()
  override val bufferSize = buffers.sumOf {
    if (it is MultipleByteBuffer) {
      it.bufferSize
    } else {
      1
    }
  }
  override val buffers: Array<out ByteBuffer> = if (bufferSize == buffers.size) {
    buffers
  } else {
    val array = arrayOfNulls<ByteBuffer>(bufferSize).uncheckedCast<Array<ByteBuffer>>()
    var index = 0
    buffers.forEach { buffer ->
      if (buffer is MultipleByteBuffer) {
        buffer.bufferIterable.forEach {
          array[index++] = it
        }
      } else {
        array[index++] = buffer
      }
    }
    array
  }

  override var capacity: Int = 0
    private set
  override var writePosition: Int = this.buffers.sumOf { it.writePosition }
  override var readPosition: Int = this.buffers.sumOf { it.readPosition }

  private var readBufIndex = 0
  private var readBuf = this.buffers[readBufIndex++]
    get() {
      while (field.readable == 0 && readBufIndex < buffers.size) {
        field = buffers[readBufIndex++]
      }
      return field
    }
  private var writeBufIndex = 0
  private var writeBuf = this.buffers[writeBufIndex++]
    get() {
      while (field.writeable == 0 && writeBufIndex < buffers.size) {
        field = buffers[writeBufIndex++]
      }
      return field
    }

  init {
    initCapacity()
  }

  private fun initCapacity() {
    var end = false
    for (i in buffers.size - 1 downTo 0) {
      if (end) {
        capacity += buffers[i].writePosition
      } else {
        capacity += buffers[i].capacity
        if (buffers[i].readable != 0) {
          writeBufIndex = i
          writeBuf = this.buffers[writeBufIndex++]
          end = true
        }
      }
    }
  }

  /**
   * ArrayByteBuffer not support append buffer for this while
   */
  override fun append(buffer: ByteBuffer) = throw UnsupportedOperationException()

  override fun get(): Byte {
    val byte = readBuf.get()
    readPosition++
    return byte
  }

  override fun put(byte: Byte) {
    writeBuf.put(byte)
    writePosition++
  }

  override fun clear() {
    buffers.forEach { buffer ->
      buffer.clear()
    }
    capacity = buffers.sumOf { it.capacity }
    readPosition = 0
    writePosition = 0
  }

  override fun reset() {
    // TODO
    var ri = 1
    var wi = 0
    var writeBuf = buffers[wi++]
    writeBuf.reset()
    W@ while (ri < buffers.size) {
      val readBuf = buffers[ri++]
      do {
        if (readBuf == writeBuf) {
          readBuf.reset()
          continue@W
        } else {
          readBuf.writeTo(writeBuf)
          if (writeBuf.writeable == 0) {
            writeBuf = buffers[wi++]
          }
        }
      } while (readBuf.readable != 0)
      readBuf.clear()
    }
    readBufIndex = 1
    writeBufIndex = wi
    this.writeBuf = writeBuf
    this.readBuf = buffers[0]
    while (wi < buffers.size) {
      capacity += writeBuf.writeable
      writeBuf = buffers[wi++]
    }
    capacity += writeBuf.writeable
  }

  override fun toString(): String {
    return "ArrayByteBuffer[$readPosition:$writePosition:$capacity]"
  }

  override fun readBufferArray(): Array<out java.nio.ByteBuffer> {
    readBuf
    writeBuf
    return buffers.getReadNioBufferList(readBufIndex - 1, writeBufIndex - readBufIndex + 1)
  }

  override fun finishRead(buffers: Array<out java.nio.ByteBuffer>) = this.buffers.finishRead(buffers, readBufIndex - 1)
    .also { readPosition += it.toInt() }

  override fun writeBufferArray(): Array<out java.nio.ByteBuffer> {
    writeBuf
    return buffers.getWriteNioBufferList(writeBufIndex - 1)
  }

  override fun finishWrite(buffers: Array<out java.nio.ByteBuffer>) =
    this.buffers.finishWrite(buffers, writeBufIndex - 1)
      .also { writePosition += it.toInt() }
}