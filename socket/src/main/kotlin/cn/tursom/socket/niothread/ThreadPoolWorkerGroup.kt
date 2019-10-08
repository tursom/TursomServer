package cn.tursom.socket.niothread

import java.nio.channels.SelectableChannel

@Suppress("MemberVisibilityCanBePrivate")
class ThreadPoolWorkerGroup(
	val poolSize: Int = Runtime.getRuntime().availableProcessors(),
	val groupName: String = "",
	override val isDaemon: Boolean = true,
	val worker: (thread: INioThread) -> Unit
) : IWorkerGroup {
	val workerGroup = Array(poolSize) {
		ThreadPoolNioThread("$groupName-$it", isDaemon = isDaemon, workLoop = worker)
	}
	var registered = 0
	override fun register(channel: SelectableChannel, onComplete: (key: SelectionContext) -> Unit) {
		val workerThread = workerGroup[registered++ % poolSize]
		workerThread.register(channel, 0) {
			onComplete(SelectionContext(it, workerThread))
		}
	}

	override fun close() {
		workerGroup.forEach {
			it.close()
			it.selector.close()
		}
	}
}