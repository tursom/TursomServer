package cn.tursom.socket

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer
import cn.tursom.core.bytebuffer.readNioBuffer
import cn.tursom.core.bytebuffer.writeNioBuffer
import cn.tursom.core.logE
import java.io.Closeable
import java.nio.ByteBuffer

interface AsyncSocket : Closeable {
  suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long
  suspend fun write(buffer: ByteBuffer, timeout: Long = 0L): Int = write(arrayOf(buffer), timeout).toInt()
  suspend fun read(buffer: ByteBuffer, timeout: Long = 0L): Int = read(arrayOf(buffer), timeout).toInt()
  override fun close()

  suspend fun write(buffer: AdvanceByteBuffer, timeout: Long = 0): Int {
    return if (buffer.bufferCount == 1) {
      buffer.readNioBuffer {
        //logE(it.toString())
        write(it, timeout)
      }
    } else {
      val readMode = buffer.readMode
      buffer.readMode()
      val value = write(buffer.nioBuffers, timeout).toInt()
      if (!readMode) buffer.resumeWriteMode()
      value
    }
  }

  suspend fun read(buffer: AdvanceByteBuffer, timeout: Long = 0): Int {
    //logE("buffer.bufferCount: ${buffer.bufferCount}")
    //logE("AsyncSocket.read(buffer: AdvanceByteBuffer, timeout: Long = 0): buffer: $buffer")
    return if (buffer.bufferCount == 1) {
      buffer.writeNioBuffer {
        //if (buffer is ByteArrayAdvanceByteBuffer) {
        //  logE(it.toString())
        //}
        read(it, timeout)
      }
    } else {
      val readMode = buffer.readMode
      buffer.resumeWriteMode()
      val value = read(buffer.nioBuffers, timeout).toInt()
      if (readMode) buffer.readMode()
      value
    }
  }
}