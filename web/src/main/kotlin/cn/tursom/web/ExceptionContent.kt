package cn.tursom.web

import cn.tursom.core.bytebuffer.AdvanceByteBuffer

interface ExceptionContent {
	val cause: Throwable

	fun write(message: String)
	fun write(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset)
	fun write(buffer: AdvanceByteBuffer) {
		write(buffer.getBytes())
	}

	fun finish()
}