package cn.tursom.socket.server

import cn.tursom.socket.BaseSocket
import java.net.ServerSocket
import java.net.SocketException

class SingleThreadSocketServer(
	private val serverSocket: ServerSocket,
	val exception: Exception.() -> Unit = { printStackTrace() },
	handler: BaseSocket.() -> Unit
) : SocketServer(handler) {
	
	constructor(
		port: Int,
		exception: Exception.() -> Unit = { printStackTrace() },
		handler: BaseSocket.() -> Unit
	) : this(ServerSocket(port), exception, handler)
	
	override fun run() {
		while (!serverSocket.isClosed) {
			try {
				serverSocket.accept().use {
					try {
						BaseSocket(it).handler()
					} catch (e: Exception) {
						e.exception()
					}
				}
			} catch (e: SocketException) {
				if (e.message == "Socket closed" || e.message == "cn.tursom.socket closed") {
					break
				} else {
					e.exception()
				}
			}
		}
	}
	
	override fun close() {
		try {
			serverSocket.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}