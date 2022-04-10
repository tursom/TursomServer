package cn.tursom.web.client

import cn.tursom.core.ByteBufferUtil
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.coroutine.GlobalScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.io.InputStream
import java.util.zip.GZIPInputStream

//TODO impl
class GzipHttpResponseStream(
  private val stream: HttpResponseStream,
) : ChannelHttpResponse() {
  private class ByteByfferInputStream(
    var byteBuffer: ByteBuffer,
  ) : InputStream() {
    override fun read(): Int {
      if (byteBuffer.readable == 0) {
        return -1
      }
      return byteBuffer.get().toInt()
    }

    override fun read(b: ByteArray): Int = byteBuffer.writeTo(b)
    override fun read(b: ByteArray, off: Int, len: Int): Int = byteBuffer.writeTo(b, off, len)
  }

  private val inputStream = ByteByfferInputStream(ByteBufferUtil.empty)

  @Suppress("BlockingMethodInNonBlockingContext")
  @OptIn(ExperimentalCoroutinesApi::class)
  override val bufferChannel: ReceiveChannel<ByteBuffer> = GlobalScope.produce {
    val gzip = GZIPInputStream(inputStream)
    while (true) {
      inputStream.byteBuffer = stream.buffer() ?: return@produce
      val bytes = gzip.readBytes()
      send(HeapByteBuffer(bytes, false))
    }
  }
}
