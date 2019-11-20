package cn.tursom.core.timer

import java.util.concurrent.*

interface Timer {
  fun exec(timeout: Long, task: () -> Unit): TimerTask

  fun runNow(task: () -> Unit) {
    threadPool.execute(task)
  }

  fun runNow(taskList: TaskQueue) {
    threadPool.execute {
      while (true) {
        val task = taskList.take() ?: return@execute
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
        var threadNumber = 0
        override fun newThread(r: Runnable): Thread {
          val thread = Thread(r)
          thread.isDaemon = true
          thread.name = "timer-worker-$threadNumber"
          return thread
        }
      })
  }
}
