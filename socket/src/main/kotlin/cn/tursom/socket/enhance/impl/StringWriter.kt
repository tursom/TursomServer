package cn.tursom.socket.enhance.impl

import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.socket.enhance.SocketWriter
import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer

class StringWriter(
    val prevWriter: SocketWriter<AdvanceByteBuffer>
) : SocketWriter<String> {
    constructor(socket: IAsyncNioSocket) : this(LengthFieldPrependWriter(socket))

    override suspend fun put(value: String, timeout: Long) {
        val buf = ByteArrayAdvanceByteBuffer(value.toByteArray())
        buf.writePosition = buf.limit
        prevWriter.put(buf, timeout)
    }

	override fun close() {
		prevWriter.close()
	}
}