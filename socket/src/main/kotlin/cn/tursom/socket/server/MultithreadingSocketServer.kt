package cn.tursom.socket.server

import cn.tursom.socket.BaseSocket
import java.net.ServerSocket

class MultithreadingSocketServer(
	private val serverSocket: ServerSocket,
	private val threadNumber: Int = cpuNumber,
	val exception: Exception.() -> Unit = {
		printStackTrace()
	},
	handler: BaseSocket.() -> Unit
) : SocketServer(handler) {
	
	constructor(
		port: Int,
		threadNumber: Int = cpuNumber,
		exception: Exception.() -> Unit = {
			printStackTrace()
		},
		handler: BaseSocket.() -> Unit
	) : this(ServerSocket(port), threadNumber, exception, handler)
	
	private val threadList = ArrayList<Thread>()
	
	override fun run() {
		for (i in 1..threadNumber) {
			val thread = Thread {
				while (true) {
					serverSocket.accept().use {
						try {
							BaseSocket(it).handler()
						} catch (e: Exception) {
							e.exception()
						}
					}
				}
			}
			thread.start()
			threadList.add(thread)
		}
	}
	
	override fun close() {
		serverSocket.close()
	}
}