package cn.tursom.socket.server

import cn.tursom.socket.BaseSocket

abstract class SocketServer(val handler: BaseSocket.() -> Unit) : ISocketServer {
	companion object {
		val cpuNumber = Runtime.getRuntime().availableProcessors() //CPU处理器的个数
	}
}