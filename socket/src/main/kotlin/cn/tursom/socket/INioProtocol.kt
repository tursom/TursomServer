package cn.tursom.socket

import cn.tursom.socket.niothread.INioThread
import java.nio.channels.SelectionKey

interface INioProtocol {
	@Throws(Throwable::class)
	fun handleConnect(key: SelectionKey, nioThread: INioThread) {
	}

	@Throws(Throwable::class)
	fun handleRead(key: SelectionKey, nioThread: INioThread)

	@Throws(Throwable::class)
	fun handleWrite(key: SelectionKey, nioThread: INioThread)

	@Throws(Throwable::class)
	fun exceptionCause(key: SelectionKey, nioThread: INioThread, e: Throwable) {
		key.cancel()
		key.channel().close()
		e.printStackTrace()
	}
}