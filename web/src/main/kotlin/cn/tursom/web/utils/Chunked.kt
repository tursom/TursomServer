package cn.tursom.web.utils

import cn.tursom.core.buffer.ByteBuffer

interface Chunked {
	val progress: Long
	val length: Long
	val endOfInput: Boolean
	fun readChunk(): ByteBuffer
	fun close()
}