package cn.tursom.web

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.nio.charset.Charset

interface WebSocketContext {
  fun writeText(buffer: ByteBuffer)
  fun writeText(bytes: ByteArray) = writeText(HeapByteBuffer(bytes))
  fun writeText(str: String, charset: Charset = Charsets.UTF_8) = writeText(str.toByteArray(charset))
  fun writeBinary(buffer: ByteBuffer)
  fun writeBinary(bytes: ByteArray) = writeBinary(HeapByteBuffer(bytes))
}