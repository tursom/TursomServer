package cn.tursom.socket.server.async

import java.io.Closeable

interface AsyncServer : Runnable, Closeable {
	val port: Int
}