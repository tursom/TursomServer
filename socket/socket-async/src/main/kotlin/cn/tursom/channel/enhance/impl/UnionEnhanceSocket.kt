package cn.tursom.channel.enhance.impl

import cn.tursom.socket.IAsyncNioSocket
import cn.tursom.channel.enhance.EnhanceSocket
import cn.tursom.channel.enhance.SocketReader
import cn.tursom.channel.enhance.SocketWriter

@Suppress("MemberVisibilityCanBePrivate")
class UnionEnhanceSocket<Read, Write>(
	val socket: IAsyncNioSocket,
	val prevReader: SocketReader<Read>,
	val prevWriter: SocketWriter<Write>
) : EnhanceSocket<Read, Write>,
	SocketReader<Read> by prevReader,
	SocketWriter<Write> by prevWriter,
	IAsyncNioSocket by socket {

	override fun close() {
		socket.close()
	}
}