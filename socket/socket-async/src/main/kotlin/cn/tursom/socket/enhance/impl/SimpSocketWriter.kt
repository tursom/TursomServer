package cn.tursom.socket.enhance.impl

import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.SocketWriter
import cn.tursom.core.bytebuffer.AdvanceByteBuffer

class SimpSocketWriter(
    val socket: IAsyncNioSocket
) : SocketWriter<AdvanceByteBuffer> {
    override suspend fun put(value: AdvanceByteBuffer, timeout: Long) {
        socket.write(value, timeout)
    }

	override fun close() {
		socket.close()
	}
}