package cn.tursom.web

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.web.utils.Chunked

interface ChunkedMessage {
  fun writeChunkedHeader()
  fun addChunked(buffer: ByteBuffer) = addChunked { buffer }
  fun addChunked(buffer: () -> ByteBuffer)
  fun finishChunked()
  fun finishChunked(chunked: Chunked)
}