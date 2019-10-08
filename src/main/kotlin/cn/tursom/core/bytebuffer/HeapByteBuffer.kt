package cn.tursom.core.bytebuffer

import java.nio.ByteBuffer

/**
 * HOOK java.nio.HeapByteBuffer
 */
object HeapByteBuffer {
	private val field = ByteBuffer::class.java.getDeclaredField("offset")

	init {
		field.isAccessible = true
	}

	fun wrap(array: ByteArray, offset: Int = 0, size: Int = array.size - offset): ByteBuffer {
		val buffer = ByteBuffer.wrap(array, 0, size + offset)
		//return if (offset == 0) buffer else {
		//	buffer.position(offset)
		//	buffer.slice()
		//}
		if (offset > 0) field.set(buffer, offset)
		return buffer
	}

	fun wrap(string: String) = wrap(string.toByteArray())
}