package cn.tursom.channel.enhance

import cn.tursom.socket.IAsyncNioSocket

interface EnhanceSocket<Read, Write> : SocketReader<Read>, SocketWriter<Write>, IAsyncNioSocket {
	override fun close()
}