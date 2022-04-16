package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.stream.impl.ByteBufferIOStream
import cn.tursom.core.stream.impl.JavaInputStream
import cn.tursom.core.stream.impl.JavaOutputStream
import cn.tursom.core.stream.impl.PairIOStream

object Stream {
  fun open(inputStream: java.io.InputStream) = JavaInputStream(inputStream)
  fun open(outputStream: java.io.OutputStream) = JavaOutputStream(outputStream)
  fun open(
    inputStream: java.io.InputStream,
    outputStream: java.io.OutputStream,
  ) = PairIOStream(JavaInputStream(inputStream), JavaOutputStream(outputStream))

  fun open(
    outputStream: java.io.OutputStream,
    inputStream: java.io.InputStream,
  ) = PairIOStream(JavaInputStream(inputStream), JavaOutputStream(outputStream))

  fun open(buffer: ByteBuffer) = ByteBufferIOStream(buffer)
  fun open(bytes: ByteArray) = open(HeapByteBuffer(bytes).also { it.clear() })
}