package cn.tursom.channel.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.channel.enhance.SocketReader

class StringReader(
	val prevReader: SocketReader<ByteBuffer>
) : SocketReader<String> {
	constructor(socket: IAsyncNioSocket) : this(LengthFieldBasedFrameReader(socket))

	override suspend fun get(buffer: ByteBuffer, timeout: Long): String {
		return prevReader.get(buffer, timeout).getString()
	}

	override fun close() {
		prevReader.close()
	}
}