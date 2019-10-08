package cn.tursom.socket.niothread

import java.io.Closeable
import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.Callable

/**
 * 一个 nio 工作线程，一个线程只有一个 Selector
 */
interface INioThread : Closeable {
	val selector: Selector
	val closed: Boolean
	val workLoop: (thread: INioThread) -> Unit
	val thread: Thread
	val isDaemon: Boolean

	fun wakeup() {
		if (Thread.currentThread() != thread) selector.wakeup()
	}

	fun register(channel: SelectableChannel, ops: Int, onComplete: (key: SelectionKey) -> Unit) {
		if (Thread.currentThread() == thread) {
			val key = channel.register(selector, ops)
			onComplete(key)
		} else {
			execute {
				val key = channel.register(selector, ops)
				onComplete(key)
			}
			wakeup()
		}
	}

	fun execute(command: Runnable)
	fun execute(command: () -> Unit) {
		execute(Runnable { command() })
	}

	fun <T> call(task: Callable<T>): T {
		return submit(task).get()
	}

	fun <T> call(task: () -> T): T {
		return call(Callable<T> { task() })
	}

	fun <T> submit(task: Callable<T>): NioThreadFuture<T>
	fun <T> submit(task: () -> T): NioThreadFuture<T> {
		return submit(Callable<T> { task() })
	}
}

