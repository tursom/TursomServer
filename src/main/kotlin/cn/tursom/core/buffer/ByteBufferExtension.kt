/**
 * 这个文件包含了一些可能出错的操作的封装
 * 还有一些是以内联形式封装以提高运行效率
 */

package cn.tursom.core.buffer

import cn.tursom.buffer.MultipleByteBuffer
import cn.tursom.core.buffer.impl.ArrayByteBuffer
import java.nio.channels.SocketChannel


/**
 * 使用读 buffer，ByteBuffer 实现类有义务维护指针正常推进
 */
inline fun <T> ByteBuffer.read(block: (java.nio.ByteBuffer) -> T): T {
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
inline fun <T> ByteBuffer.write(block: (java.nio.ByteBuffer) -> T): T {
  val buffer = writeBuffer()
  return try {
    block(buffer)
  } finally {
    finishWrite(buffer)
  }
}

fun SocketChannel.read(buffer: ByteBuffer): Int {
  return if (buffer is MultipleByteBuffer) {
    buffer.writeBuffers { read(it) }.toInt()
  } else {
    buffer.write { read(it) }
  }
}

fun SocketChannel.write(buffer: ByteBuffer): Int {
  return if (buffer is MultipleByteBuffer) {
    buffer.readBuffers { write(it) }.toInt()
  } else {
    buffer.read { write(it) }
  }
}

fun SocketChannel.read(buffer: MultipleByteBuffer): Long {
  return buffer.writeBuffers { read(it) }
}

fun SocketChannel.write(buffer: MultipleByteBuffer): Long {
  return buffer.readBuffers { write(it) }
}

fun SocketChannel.read(buffers: Array<out ByteBuffer>): Long {
  val bufferArray = Array(buffers.size) { buffers[it].writeBuffer() }
  return try {
    read(bufferArray)
  } finally {
    buffers.forEachIndexed { index, byteBuffer -> byteBuffer.finishWrite(bufferArray[index]) }
  }
}

fun SocketChannel.write(buffers: Array<out ByteBuffer>): Long {
  val bufferArray = Array(buffers.size) { buffers[it].readBuffer() }
  return try {
    write(bufferArray)
  } finally {
    buffers.forEachIndexed { index, byteBuffer -> byteBuffer.finishRead(bufferArray[index]) }
  }
}

fun Array<out ByteBuffer>.asMultipleByteBuffer() = ArrayByteBuffer(*this)