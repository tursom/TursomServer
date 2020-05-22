package cn.tursom.channel.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.channel.enhance.SocketReader

class SimpSocketReader(
	val socket: IAsyncNioSocket
) : SocketReader<ByteBuffer> {
	override suspend fun get(buffer: ByteBuffer, timeout: Long): ByteBuffer {
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