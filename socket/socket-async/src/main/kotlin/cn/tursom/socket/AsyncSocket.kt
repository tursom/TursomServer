package cn.tursom.socket

import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import java.io.Closeable
import java.nio.ByteBuffer

interface AsyncSocket : Closeable {
  suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun write(buffer: ByteBuffer, timeout: Long = 0L): Int = write(arrayOf(buffer), timeout).toInt()
  suspend fun read(buffer: ByteBuffer, timeout: Long = 0L): Int = read(arrayOf(buffer), timeout).toInt()
  override fun close()

  suspend fun write(buffer: cn.tursom.core.buffer.ByteBuffer, timeout: Long = 0): Int {
    return buffer.read {
      write(it, timeout)
    }
  }

  suspend fun read(buffer: cn.tursom.core.buffer.ByteBuffer, timeout: Long = 0): Int {
    return buffer.write {
      read(it, timeout)
    }
  }

  suspend fun write(buffers: Array<out cn.tursom.core.buffer.ByteBuffer>, timeout: Long): Long {
    val nioBuffer = buffers.map { it.readBuffer() }.toTypedArray()
    val writeSize = write(nioBuffer, timeout)
    buffers.forEachIndexed { index, byteBuffer -> byteBuffer.finishRead(nioBuffer[index]) }
    return writeSize
  }

  suspend fun write(buffers: Collection<cn.tursom.core.buffer.ByteBuffer>, timeout: Long): Long {
    val nioBuffer = buffers.map { it.readBuffer() }.toTypedArray()
    val writeSize = write(nioBuffer, timeout)
    buffers.forEachIndexed { index, byteBuffer -> byteBuffer.finishRead(nioBuffer[index]) }
    return writeSize
  }
}