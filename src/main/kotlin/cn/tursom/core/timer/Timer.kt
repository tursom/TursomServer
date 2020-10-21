package cn.tursom.core.timer

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

interface Timer {
  fun exec(timeout: Long, task: () -> Unit): TimerTask

  fun runNow(task: () -> Unit) {
    threadPool.execute(task)
  }

  fun runNow(taskList: TaskQueue) {
    threadPool.execute {
      while (true) {
        val task = taskList.take() ?: break
        try {
          task()
        } catch (e: Throwable) {
          e.printStackTrace()
        }
      }
    }
  }

  companion object {
    private val threadPool: ExecutorService = ThreadPoolExecutor(
      Runtime.getRuntime().availableProcessors(),
      Runtime.getRuntime().availableProcessors(),
      0L, TimeUnit.MILLISECONDS,
      LinkedTransferQueue(),
      object : ThreadFactory {
        var threadNumber = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
          val thread = Thread(r)
          thread.isDaemon = true
          thread.name = "timer-worker-${threadNumber.incrementAndGet()}"
          return thread
        }
      })
  }
}
