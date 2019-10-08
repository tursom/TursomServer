package cn.tursom.core.bytebuffer

import java.nio.ByteBuffer

class DirectNioAdvanceByteBuffer(val buffer: ByteBuffer) : AdvanceByteBuffer {
	override val nioBuffer: ByteBuffer get() = buffer
	override val readOnly: Boolean get() = buffer.isReadOnly
	override var writePosition: Int = buffer.position()
		get() = field
		set(value) {
			if (!readMode) buffer.position(value)
			field = value
		}
	override var limit: Int = buffer.limit()
		get() = if (!readMode) buffer.limit() else field
		set(value) {
			if (!readMode) buffer.limit(value)
			field = value
		}
	override val capacity: Int get() = buffer.capacity()

	override val hasArray: Boolean get() = false
	override val array: ByteArray get() = buffer.array()
	override val arrayOffset: Int = 0
	override var readPosition: Int = 0
		get() = if (readMode) buffer.position() else field
		set(value) {
			if (readMode) buffer.position(value)
			field = value
		}
	override val readableSize: Int get() = if (readMode) buffer.remaining() else writePosition - readPosition
	override val size: Int get() = buffer.capacity()
	override var readMode: Boolean = false

	override fun readMode() {
		if (!readMode) {
			readMode = true
			buffer.flip()
		}
	}

	override fun resumeWriteMode(usedSize: Int) {
		if (readMode) {
			readMode = false
			buffer.limit(capacity)
			buffer.position(writePosition)
		}
	}

	override fun clear() {
		buffer.clear()
	}
}