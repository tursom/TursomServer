package cn.tursom.socket.enhance.impl

import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.SocketReader
import cn.tursom.core.bytebuffer.AdvanceByteBuffer

class SimpSocketReader(
	val socket: IAsyncNioSocket
) : SocketReader<AdvanceByteBuffer> {
	override suspend fun get(buffer: AdvanceByteBuffer, timeout: Long): AdvanceByteBuffer {
		buffer.reset()
		if (socket.read(buffer) < 0) {
			socket.close()
		}
		return buffer
	}

	override fun close() {
		socket.close()
	}
}