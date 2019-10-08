package cn.tursom.socket.enhance

import java.io.Closeable

interface SocketWriter<T> : Closeable {
	suspend fun put(value: T, timeout: Long = 0)
}