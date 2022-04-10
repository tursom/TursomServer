package cn.tursom.web.client

import cn.tursom.core.ByteBufferUtil
import cn.tursom.core.coroutine.GlobalScope
import com.aayushatharva.brotli4j.decoder.DecoderJNI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce

class BrotliHttpResponseStream(
  private val stream: HttpResponseStream,
  inputBufferSize: Int = 8 * 1024,
) : ChannelHttpResponse() {
  private val decoder = DecoderJNI.Wrapper(inputBufferSize)

  @OptIn(ExperimentalCoroutinesApi::class)
  override val bufferChannel = GlobalScope.produce {
    while (true) {
      val input = stream.buffer() ?: return@produce
      when (decoder.status) {
        DecoderJNI.Status.DONE -> return@produce
        DecoderJNI.Status.OK -> decoder.push(0)
        DecoderJNI.Status.NEEDS_MORE_INPUT -> {
          if (decoder.hasOutput()) {
            val buffer = decoder.pull()
            send(ByteBufferUtil.wrap(buffer, false))
          }
          val decoderInputBuffer = decoder.inputBuffer
          decoderInputBuffer.clear()
          input.writeTo(ByteBufferUtil.wrap(decoderInputBuffer, true))
          //decoderInputBuffer.put(input.getBytes(decoderInputBuffer.limit() - decoderInputBuffer.position()))
          decoder.push(decoderInputBuffer.position())
        }
        DecoderJNI.Status.NEEDS_MORE_OUTPUT -> {
          val buffer = decoder.pull() ?: continue
          send(ByteBufferUtil.wrap(buffer, false))
        }
        else -> return@produce
      }
    }
  }
}

