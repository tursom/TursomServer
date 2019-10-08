package cn.tursom.socket.enhance.impl

import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.SocketReader
import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer

class LengthFieldBasedFrameReader(
	val prevReader: SocketReader<AdvanceByteBuffer>
) : SocketReader<AdvanceByteBuffer> {
	constructor(socket: IAsyncNioSocket) : this(SimpSocketReader(socket))

	override suspend fun get(buffer: AdvanceByteBuffer, timeout: Long): AdvanceByteBuffer {
		val rBuf = prevReader.get(buffer, timeout)
		val blockSize = rBuf.getInt()
		if (rBuf.readableSize == blockSize) {
			return rBuf
		} else if (rBuf.readableSize > blockSize) {
			return if (rBuf.hasArray) {
				val retBuf = ByteArrayAdvanceByteBuffer(rBuf.array, rBuf.readOffset, blockSize)
				rBuf.readPosition += blockSize
				retBuf
			} else {
				val targetBuffer = ByteArrayAdvanceByteBuffer(blockSize)
				rBuf.writeTo(targetBuffer)
				targetBuffer
			}
		}

		val targetBuffer = ByteArrayAdvanceByteBuffer(blockSize)
		rBuf.writeTo(targetBuffer)
		while (targetBuffer.writeableSize != 0) {
			val rBuf2 = prevReader.get(buffer, timeout)
			if (rBuf2.readableSize == 0) return targetBuffer
			rBuf2.writeTo(targetBuffer)
		}
		return targetBuffer
	}

	override fun close() {
		prevReader.close()
	}
}


