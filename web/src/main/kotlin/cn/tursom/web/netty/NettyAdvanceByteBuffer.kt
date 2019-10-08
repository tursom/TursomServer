package cn.tursom.web.netty

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import io.netty.buffer.ByteBuf
import java.io.OutputStream
import java.nio.ByteBuffer

class NettyAdvanceByteBuffer(val byteBuf: ByteBuf) : AdvanceByteBuffer {
	override val hasArray: Boolean get() = byteBuf.hasArray()
	override val readOnly: Boolean get() = byteBuf.isReadOnly
	override val nioBuffer: ByteBuffer
		get() = if (readMode) byteBuf.nioBuffer()
		else byteBuf.nioBuffer(writePosition, limit)

	override val bufferCount: Int get() = byteBuf.nioBufferCount()
	override val nioBuffers: Array<out ByteBuffer> get() = byteBuf.nioBuffers()

	override var writePosition: Int
		get() = byteBuf.writerIndex()
		set(value) {
			byteBuf.writerIndex(value)
		}
	override var limit: Int
		get() = byteBuf.capacity()
		set(value) {
			byteBuf.capacity(value)
		}
	override val capacity get() = byteBuf.maxCapacity()
	override val array: ByteArray get() = byteBuf.array()
	override val arrayOffset: Int get() = byteBuf.arrayOffset()
	override var readPosition: Int
		get() = byteBuf.readerIndex()
		set(value) {
			byteBuf.readerIndex(value)
		}
	override val readOffset: Int get() = byteBuf.arrayOffset() + byteBuf.readerIndex()
	override val readableSize: Int
		get() = byteBuf.readableBytes()
	override val available: Int get() = readableSize
	override val writeOffset: Int get() = writePosition
	override val writeableSize: Int
		get() = limit - writePosition
	override val size: Int get() = capacity
	override var readMode: Boolean = false

	override fun readMode() {
		readMode = true
	}

	override fun resumeWriteMode(usedSize: Int) {
		readPosition += usedSize
		readMode = false
	}

	override fun needReadSize(size: Int) {
		if (size > limit) {
			if (size < capacity) byteBuf.capacity(size)
			else throw IndexOutOfBoundsException()
		}
	}

	override fun clear() {
		byteBuf.clear()
	}

	override fun reset() {
		byteBuf.discardReadBytes()
	}

	override fun reset(outputStream: OutputStream) {
		byteBuf.readBytes(outputStream, readableSize)
		byteBuf.clear()
	}

	override fun get(): Byte = byteBuf.readByte()
	override fun getChar(): Char = byteBuf.readChar()
	override fun getShort(): Short = byteBuf.readShort()
	override fun getInt(): Int = byteBuf.readInt()
	override fun getLong(): Long = byteBuf.readLong()
	override fun getFloat(): Float = byteBuf.readFloat()
	override fun getDouble(): Double = byteBuf.readDouble()

	override fun getBytes(): ByteArray {
		val bytes = ByteArray(byteBuf.readableBytes())
		byteBuf.readBytes(bytes)
		return bytes
	}

	override fun getString(size: Int): String {
		val str = byteBuf.toString(readPosition, size, Charsets.UTF_8)
		readPosition += size
		return str
	}

	override fun writeTo(buffer: ByteArray, bufferOffset: Int, size: Int): Int {
		byteBuf.readBytes(buffer, bufferOffset, size)
		return size
	}

	override fun writeTo(os: OutputStream): Int {
		val size = readableSize
		byteBuf.readBytes(os, size)
		reset()
		return size
	}

	override fun put(byte: Byte) {
		byteBuf.writeByte(byte.toInt())
	}

	override fun put(char: Char) {
		byteBuf.writeChar(char.toInt())
	}

	override fun put(short: Short) {
		byteBuf.writeShort(short.toInt())
	}

	override fun put(int: Int) {
		byteBuf.writeInt(int)
	}

	override fun put(long: Long) {
		byteBuf.writeLong(long)
	}

	override fun put(float: Float) {
		byteBuf.writeFloat(float)
	}

	override fun put(double: Double) {
		byteBuf.writeDouble(double)
	}

	override fun put(str: String) {
		byteBuf.writeCharSequence(str, Charsets.UTF_8)
	}

	override fun put(byteArray: ByteArray, startIndex: Int, endIndex: Int) {
		byteBuf.writeBytes(byteArray, startIndex, endIndex - startIndex)
	}

}