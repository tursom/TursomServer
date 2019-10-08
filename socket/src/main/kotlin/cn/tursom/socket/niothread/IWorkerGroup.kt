package cn.tursom.socket.niothread

import java.io.Closeable
import java.nio.channels.SelectableChannel

interface IWorkerGroup : Closeable {
	val isDaemon: Boolean
	fun register(channel: SelectableChannel, onComplete: (key: SelectionContext) -> Unit)
}
