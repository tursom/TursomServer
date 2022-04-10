package cn.tursom.web.client.okhttp

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.web.client.HttpResponseStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class OkhttpResponseStream(
  val body: ResponseBody?,
) : HttpResponseStream {
  override suspend fun buffer(): ByteBuffer? {
    val stream = body?.byteStream() ?: return null
    val buffer = HeapByteBuffer(1024)
    val read = withContext(Dispatchers.IO) {
      stream.read(buffer.array, buffer.writeOffset, buffer.writeable)
    }
    if (read == 0) {
      return null
    }
    buffer.writePosition += read
    return buffer
  }

  override suspend fun skip(n: Long) = withContext(Dispatchers.IO) {
    body?.byteStream()?.skip(n) ?: 0
  }

  override suspend fun read(): Int = withContext(Dispatchers.IO) {
    body?.byteStream()?.read() ?: -1
  }

  override suspend fun read(buffer: ByteBuffer) {
    body ?: return
    withContext(Dispatchers.IO) {
      buffer.put(body.byteStream())
    }
  }

  override fun close() {
    body?.close()
  }

  override suspend fun readBytes(): ByteArray {
    return withContext(Dispatchers.IO) {
      body?.bytes() ?: ByteArray(0)
    }
  }

  override suspend fun string(): String {
    return withContext(Dispatchers.IO) {
      body?.string() ?: ""
    }
  }
}
