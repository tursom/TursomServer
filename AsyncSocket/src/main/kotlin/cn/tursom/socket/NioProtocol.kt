package cn.tursom.socket

import cn.tursom.niothread.NioThread
import java.nio.channels.SelectionKey

interface NioProtocol {
	@Throws(Throwable::class)
	fun handleConnect(key: SelectionKey, nioThread: NioThread) {
	}

	@Throws(Throwable::class)
	fun handleRead(key: SelectionKey, nioThread: NioThread)

	@Throws(Throwable::class)
	fun handleWrite(key: SelectionKey, nioThread: NioThread)

	@Throws(Throwable::class)
	fun exceptionCause(key: SelectionKey, nioThread: NioThread, e: Throwable) {
		key.cancel()
		key.channel().close()
		e.printStackTrace()
	}
}