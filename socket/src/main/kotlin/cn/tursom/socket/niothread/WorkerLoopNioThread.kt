package cn.tursom.socket.niothread

import cn.tursom.core.timer.WheelTimer
import java.nio.channels.Selector
import java.util.concurrent.Callable
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class WorkerLoopNioThread(
	val threadName: String = "nioLoopThread",
	override val selector: Selector = Selector.open(),
	override val isDaemon: Boolean = false,
	override val workLoop: (thread: INioThread) -> Unit
) : INioThread {
	private var onWakeup: AtomicBoolean = AtomicBoolean(false)

	override var closed: Boolean = false

	val waitQueue = LinkedBlockingDeque<Runnable>()
	val taskQueue = LinkedBlockingDeque<Future<Any?>>()

	override val thread = Thread {
		while (!closed) {
			try {
				workLoop(this)
			} catch (e: Exception) {
				e.printStackTrace()
			}
			//System.err.println("$threadName worker loop finish once")
			while (waitQueue.isNotEmpty()) try {
				waitQueue.poll().run()
			} catch (e: Exception) {
				e.printStackTrace()
			}
			while (taskQueue.isNotEmpty()) try {
				val task = taskQueue.poll()
				try {
					task.resume(task.task.call())
				} catch (e: Throwable) {
					task.resumeWithException(e)
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	init {
		thread.name = threadName
		thread.isDaemon = isDaemon
		thread.start()
	}

	override fun execute(command: Runnable) {
		waitQueue.add(command)
	}

	override fun <T> submit(task: Callable<T>): NioThreadFuture<T> {
		val f = Future(task)
		@Suppress("UNCHECKED_CAST")
		taskQueue.add(f as Future<Any?>)
		return f
	}

	override fun close() {
		closed = true
	}

	override fun wakeup() {
		if (Thread.currentThread() != thread && onWakeup.compareAndSet(false, true)) {
			timer.exec(50) {
				onWakeup.set(false)
				selector.wakeup()
			}
		}
	}

	class Future<T>(val task: Callable<T>) : NioThreadFuture<T> {
		private val lock = Object()
		private var exception: Throwable? = null
		private var result: Pair<T, Boolean>? = null

		override fun get(): T {
			val result = this.result
			return when {
				exception != null -> throw RuntimeException(exception)
				result != null -> result.first
				else -> synchronized(lock) {
					lock.wait()
					val exception = this.exception
					if (exception != null) {
						throw RuntimeException(exception)
					} else {
						this.result!!.first
					}
				}
			}
		}

		fun resume(value: T) {
			result = value to true
			synchronized(lock) {
				lock.notifyAll()
			}
		}

		fun resumeWithException(e: Throwable) {
			exception = e
			synchronized(lock) {
				lock.notifyAll()
			}
		}
	}

	companion object {
		val timer = WheelTimer.smoothTimer
	}
}