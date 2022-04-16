package cn.tursom.channel

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.ArrayByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

interface WritableAsyncChannel {
  suspend fun write(buffer: ByteBuffer, timeout: Long = 0L): Long

  suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long = 0L): Long = write(buffer, 0, buffer.size, timeout)
  suspend fun write(
    buffer: Array<out ByteBuffer>,
    offset: Int,
    length: Int,
    timeout: Long = 0L,
  ): Long = write(ArrayByteBuffer(buffer))

  suspend fun write(
    file: FileChannel,
    position: Long,
    count: Long,
    timeout: Long = 0,
  ): Long

  suspend fun write(bytes: ByteArray, offset: Int = 0, len: Int = bytes.size - offset): Long {
    return write(HeapByteBuffer(bytes, offset, len))
  }

  suspend fun write(str: String, charset: Charset = Charsets.UTF_8): Long {
    return write(str.toByteArray(charset))
  }
}
