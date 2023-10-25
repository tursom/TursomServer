package cn.tursom.web.client

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.util.fromJson
import cn.tursom.core.util.fromJsonTyped
import cn.tursom.core.util.toUTF8String
import java.io.ByteArrayOutputStream
import java.io.Closeable

interface HttpResponseStream : Closeable {
  suspend fun buffer(): ByteBuffer?
  suspend fun skip(n: Long): Long
  suspend fun read(): Int
  suspend fun read(buffer: ByteBuffer)
  suspend fun read(
    buffer: ByteArray,
    offset: Int = 0,
    len: Int = buffer.size - offset,
  ): Int {
    val byteBuffer = HeapByteBuffer(buffer, offset, len)
    byteBuffer.clear()
    read(byteBuffer)
    return byteBuffer.writePosition
  }

  suspend fun readBytes(): ByteArray {
    val os = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    do {
      val read = read(buffer)
      os.write(buffer, 0, read)
    } while (read != 0)
    return os.toByteArray()
  }

  suspend fun string() = readBytes().toUTF8String()
}

suspend inline fun <reified T : Any> HttpResponseStream.json(): T = string().fromJson()
suspend inline fun <reified T : Any> HttpResponseStream.jsonGeneric(): T = string().fromJsonTyped()
