package cn.tursom.channel.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.channel.enhance.SocketReader

class LengthFieldBasedFrameReader(
	val prevReader: SocketReader<ByteBuffer>
) : SocketReader<ByteBuffer> {
	constructor(socket: IAsyncNioSocket) : this(SimpSocketReader(socket))

	override suspend fun get(buffer: ByteBuffer, timeout: Long): ByteBuffer {
		val rBuf = prevReader.get(buffer, timeout)
		val blockSize = rBuf.getInt()
		if (rBuf.readable == blockSize) {
			return rBuf
		} else if (rBuf.readable > blockSize) {
			return if (rBuf.hasArray) {
				val retBuf = HeapByteBuffer(rBuf.array, rBuf.readOffset, blockSize)
				rBuf.readPosition += blockSize
				retBuf
			} else {
				val targetBuffer = HeapByteBuffer(blockSize)
				rBuf.writeTo(targetBuffer)
				targetBuffer
			}
		}

		val targetBuffer = HeapByteBuffer(blockSize)
		rBuf.writeTo(targetBuffer)
		while (targetBuffer.writeable != 0) {
			val rBuf2 = prevReader.get(buffer, timeout)
			if (rBuf2.readable == 0) return targetBuffer
			rBuf2.writeTo(targetBuffer)
		}
		return targetBuffer
	}

	override fun close() {
		prevReader.close()
	}
}


