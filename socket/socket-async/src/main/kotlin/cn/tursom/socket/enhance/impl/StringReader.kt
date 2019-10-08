package cn.tursom.socket.enhance.impl

import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.SocketReader
import cn.tursom.core.bytebuffer.AdvanceByteBuffer

class StringReader(
	val prevReader: SocketReader<AdvanceByteBuffer>
) : SocketReader<String> {
	constructor(socket: IAsyncNioSocket) : this(LengthFieldBasedFrameReader(socket))

	override suspend fun get(buffer: AdvanceByteBuffer, timeout: Long): String {
		return prevReader.get(buffer, timeout).getString()
	}

	override fun close() {
		prevReader.close()
	}
}