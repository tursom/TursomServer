package cn.tursom.socket.niothread

import cn.tursom.core.timer.WheelTimer
import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("MemberVisibilityCanBePrivate")
class ThreadPoolNioThread(
	val threadName: String = "",
	override val selector: Selector = Selector.open(),
	override val isDaemon: Boolean = true,
	override val workLoop: (thread: INioThread) -> Unit
) : INioThread {
	private var onWakeup: AtomicBoolean = AtomicBoolean(false)
	override lateinit var thread: Thread
	//val threadPool: ExecutorService = Executors.newSingleThreadExecutor {
	//	val thread = Thread(it)
	//	workerThread = thread
	//	thread.isDaemon = true
	//	thread.name = threadName
	//	thread
	//}
	val threadPool: ExecutorService = ThreadPoolExecutor(1, 1,
		0L, TimeUnit.MILLISECONDS,
		LinkedBlockingQueue<Runnable>(),
		ThreadFactory {
			val thread = Thread(it)
			this.thread = thread
			thread.isDaemon = isDaemon
			thread.name = threadName
			thread
		})

	init {
		threadPool.execute(object : Runnable {
			override fun run() {
				workLoop(this@ThreadPoolNioThread)
				if (!threadPool.isShutdown) threadPool.execute(this)
			}
		})
	}

	override var closed: Boolean = false

	override fun wakeup() {
		if (Thread.currentThread() != thread && onWakeup.compareAndSet(false, true)) {
			timer.exec(50) {
				onWakeup.set(false)
				selector.wakeup()
			}
		}
	}

	override fun register(channel: SelectableChannel, ops: Int, onComplete: (key: SelectionKey) -> Unit) {
		if (Thread.currentThread() == thread) {
			onComplete(channel.register(selector, ops))
		} else {
			threadPool.execute { register(channel, ops, onComplete) }
			wakeup()
		}
	}

	override fun execute(command: Runnable) = threadPool.execute(command)
	override fun <T> call(task: Callable<T>): T = threadPool.submit(task).get()
	override fun <T> submit(task: Callable<T>): NioThreadFuture<T> = ThreadPoolFuture(threadPool.submit(task))

	override fun close() {
		closed = true
		threadPool.shutdown()
	}

	class ThreadPoolFuture<T>(val future: Future<T>) : NioThreadFuture<T> {
		override fun get(): T = future.get()
	}

	override fun toString(): String {
		return "SingleThreadNioThread($threadName)"
	}

	companion object {
		val timer = WheelTimer.smoothTimer
	}
}