package cn.tursom.web.client

import cn.tursom.core.buffer.ByteBuffer
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.ByteArrayOutputStream


abstract class ChannelHttpResponse : HttpResponseStream {
  protected abstract val bufferChannel: ReceiveChannel<ByteBuffer>
  private var buffer: ByteBuffer? = null

  override suspend fun buffer(): ByteBuffer? {
    while (buffer == null || buffer?.readable == 0) {
      buffer?.close()
      val receive = bufferChannel.receiveCatching()
      buffer = if (receive.isSuccess) {
        receive.getOrThrow()
      } else {
        val e = receive.exceptionOrNull()
        if (e != null) {
          throw e
        }
        return null
      }
    }
    return buffer
  }

  override suspend fun skip(n: Long): Long {
    var skip = 0L
    while (skip < n) {
      val buffer = buffer() ?: return skip
      skip += buffer.skip((n - skip).toInt())
    }
    return skip
  }

  override suspend fun read(): Int {
    val buffer = buffer() ?: return -1
    return buffer.get().toInt()
  }

  override suspend fun read(buffer: ByteBuffer) {
    val buf = buffer() ?: return
    buf.writeTo(buffer)
  }

  override fun close() {
    bufferChannel.cancel()
  }

  override suspend fun readBytes(): ByteArray {
    val os = ByteArrayOutputStream()
    var buf = buffer()
    while (buf != null) {
      buf.writeTo(os)
      buf = buffer()
    }
    return os.toByteArray()
  }
}