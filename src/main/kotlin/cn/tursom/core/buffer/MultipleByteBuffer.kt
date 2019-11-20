package cn.tursom.buffer

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.ListByteBuffer
import java.io.Closeable

@Suppress("unused")
interface MultipleByteBuffer : List<ByteBuffer>, Closeable {
  val buffers: Array<out ByteBuffer> get() = toTypedArray()

  /**
   * 使用读 buffer，ByteBuffer 实现类有义务维护指针正常推进
   */
  fun <T> readBuffers(block: (Array<out java.nio.ByteBuffer>) -> T): T {
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
  fun <T> writeBuffers(block: (Array<out java.nio.ByteBuffer>) -> T): T {
    val buffer = writeBuffers()
    return try {
      block(buffer)
    } finally {
      finishWrite(buffer)
    }
  }

  fun readBuffers(): Array<out java.nio.ByteBuffer> = Array(size) { this[it].readBuffer() }
  fun writeBuffers(): Array<out java.nio.ByteBuffer> = Array(size) { this[it].writeBuffer() }

  fun finishRead(buffers: Array<out java.nio.ByteBuffer>) = buffers.forEachIndexed { index, byteBuffer ->
    this[index].finishRead(byteBuffer)
  }

  fun finishWrite(buffers: Array<out java.nio.ByteBuffer>) = buffers.forEachIndexed { index, byteBuffer ->
    this[index].finishWrite(byteBuffer)
  }

  override fun close() = forEach(ByteBuffer::close)
  fun slice(offset: Int, size: Int): MultipleByteBuffer = ListByteBuffer(subList(offset, offset + size))
  fun fill(byte: Byte) = forEach { it.fill(byte) }
  fun clear() = forEach(ByteBuffer::clear)
  fun reset() = forEach(ByteBuffer::reset)
}