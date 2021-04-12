package cn.tursom.core

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class ScheduledExecutorPool(
  private val threadCount: Int = Runtime.getRuntime().availableProcessors() * 2,
  private val threadFactory: ThreadFactory = Executors.defaultThreadFactory(),
) {
  private val scheduledExecutorQueue = ConcurrentLinkedDeque<Pair<Thread, ScheduledExecutorService>>()
  private var initCount = AtomicInteger()

  init {
    initOne()
  }

  private fun initOne() {
    if (initCount.incrementAndGet() < threadCount) {
      val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor {
        threadFactory.newThread(it)
      }
      val countDownLatch = CountDownLatch(1)
      executor.execute {
        scheduledExecutorQueue.addFirst(Thread.currentThread() to executor)
        countDownLatch.countDown()
      }
      countDownLatch.await(3, TimeUnit.SECONDS)
    } else {
      initCount.decrementAndGet()
    }
  }

  fun get(): Pair<Thread, ScheduledExecutorService> {
    if (initCount.get() < threadCount) {
      initOne()
    }
    val pair = scheduledExecutorQueue.poll()
    scheduledExecutorQueue.add(pair)
    return pair
  }
}