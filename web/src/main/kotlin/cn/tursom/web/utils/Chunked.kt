package cn.tursom.web.utils

import cn.tursom.core.bytebuffer.AdvanceByteBuffer

interface Chunked {
	val progress: Long
	val length: Long
	val endOfInput: Boolean
	fun readChunk(): AdvanceByteBuffer
	fun close()
}