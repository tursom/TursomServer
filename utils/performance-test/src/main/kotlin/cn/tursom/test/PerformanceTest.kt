package cn.tursom.test

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class PerformanceTest(
  val countPerThread: Int,
  val threadCount: Int = 1,
  val handler: PerformanceTestHandler
) {
  val totalCount = countPerThread * threadCount

  constructor(
    countPerThread: Int,
    threadCount: Int = 1,
    handler: () -> Unit
  ) : this(countPerThread, threadCount, object : PerformanceTestHandler, () -> Unit by handler {})

  fun run(): TestResult {
    val schedule = AtomicInteger(0)
    val wait = CountDownLatch(threadCount)
    val runLock = CountDownLatch(1)
    var line95 = 0L
    var line98 = 0L
    var line99 = 0L
    repeat(threadCount) {
      thread {
        runLock.await()
        try {
          repeat(countPerThread) {
            val loop = schedule.getAndIncrement()
            if (loop * 100 % totalCount == 0) {
              val line = loop * 100 / totalCount
              when (line) {
                95 -> line95 = System.currentTimeMillis()
                98 -> line98 = System.currentTimeMillis()
                99 -> line99 = System.currentTimeMillis()
              }
              handler.logSchedule(line)
            }
            handler()
          }
        } finally {
          wait.countDown()
        }
      }
    }
    val start = System.currentTimeMillis()
    runLock.countDown()
    wait.await()
    val end = System.currentTimeMillis()
    return TestResult(start, end, line95, line98, line99)
  }
}
