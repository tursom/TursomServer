package cn.tursom.datagram.server

import java.io.Closeable

interface UDPServer : Runnable, Closeable {
	val port: Int
	
	fun start()
}

