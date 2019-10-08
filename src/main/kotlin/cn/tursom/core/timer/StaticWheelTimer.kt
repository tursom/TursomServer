package cn.tursom.core.timer

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.concurrent.thread

class StaticWheelTimer(
	val tick: Long = 200,
	val wheelSize: Int = 512
) : Timer {
	var closed = false
	val taskQueueArray = Array(wheelSize) { TaskQueue() }
	private var position = 0

	override fun exec(timeout: Long, task: () -> Unit): TimerTask {
		val index = ((timeout / tick + position + if (timeout % tick == 0L) 0 else 1) % wheelSize).toInt()
		return taskQueueArray[index].offer(task, timeout)
	}

	init {
		thread(isDaemon = true) {
			while (!closed) {
				position %= wheelSize

				val newQueue = TaskQueue()
				val taskQueue = taskQueueArray[position]
				taskQueueArray[position] = newQueue

				val time = System.currentTimeMillis()
				var node = taskQueue.root
				while (node != null) {
					node = if (node.isOutTime(time)) {
						if (!node.canceled) threadPool.execute(node.task)
						node.next
					} else {
						val next = node.next
						newQueue.offer(node)
						next
					}
				}

				position++
				Thread.sleep(tick)
			}
		}
	}


	class TaskQueue {
		var root: TaskNode? = null

		fun offer(task: () -> Unit, timeout: Long): TaskNode {
			synchronized(this) {
				val insert = TaskNode(timeout, task, root)
				root = insert
				return insert
			}
		}

		fun offer(node: TaskNode): TaskNode {
			synchronized(this) {
				node.next = root
				root = node
				return node
			}
		}

		inner class TaskNode(
			val timeout: Long,
			val task: () -> Unit,
			var next: TaskNode?,
			var canceled: Boolean = false
		) : TimerTask {
			val outTime = System.currentTimeMillis() + timeout
			val isOutTime get() = System.currentTimeMillis() > outTime

			fun isOutTime(time: Long) = time > outTime

			override fun run() = task()

			override fun cancel() {
				canceled = true
			}
		}
	}

	companion object {
		val threadPool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
			object : ThreadFactory {
				var threadNumber = 0
				override fun newThread(r: Runnable): Thread {
					val thread = Thread(r)
					thread.isDaemon = true
					thread.name = "staticWheelTimerWorker-$threadNumber"
					return thread
				}
			})
		val timer by lazy { StaticWheelTimer(100, 1024) }
		val smoothTimer by lazy { StaticWheelTimer(20, 128) }
	}
}