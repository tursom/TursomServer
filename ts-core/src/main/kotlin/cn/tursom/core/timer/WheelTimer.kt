package cn.tursom.core.timer

import cn.tursom.core.CurrentTimeMillisClock
import java.lang.Thread.sleep
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.concurrent.thread


@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class WheelTimer(
  val tick: Long = 200,
  val wheelSize: Int = 512,
  val name: String = "wheelTimerLooper",
  val taskQueueFactory: () -> TaskQueue = { NonLockTaskQueue() },
) : Timer {
  var closed = false
  val taskQueueArray = AtomicReferenceArray(Array(wheelSize) { taskQueueFactory() })

  @Volatile
  private var position = 0

  override fun exec(timeout: Long, task: () -> Unit): TimerTask {
    //val index = ((timeout / tick + position + if (timeout % tick == 0L) 0 else 1) % wheelSize).toInt()
    val index = ((timeout / tick + position) % wheelSize).toInt()
    return taskQueueArray[index].offer(task, timeout)
  }

  init {
    //ScheduledThreadPoolExecutor(1) { runnable ->
    //  val thread = Thread(runnable, name)
    //  thread.isDaemon = true
    //  thread
    //}.scheduleAtFixedRate(tick, tick, TimeUnit.MILLISECONDS) {
    //  val outTimeQueue = taskQueueFactory()
    //  val newQueue = taskQueueFactory()
    //  val taskQueue = taskQueueArray.getAndSet(position++, newQueue)
    //  position %= wheelSize
    //  while (true) {
    //    val task = taskQueue.take() ?: break
    //    if (task.canceled) {
    //
    //      continue
    //    } else if (task.isOutTime) {
    //      outTimeQueue.offer(task)
    //    } else {
    //      newQueue.offer(task)
    //    }
    //  }
    //
    //  runNow(outTimeQueue)
    //}
    thread(isDaemon = true, name = name) {
      var startTime = CurrentTimeMillisClock.now
      while (!closed) {
        position %= wheelSize

        val outTimeQueue = taskQueueFactory()
        val newQueue = taskQueueFactory()
        val taskQueue = taskQueueArray.getAndSet(position++, newQueue)

        while (true) {
          val node = taskQueue.take() ?: break
          if (node.canceled) {
            continue
          } else if (node.isOutTime) {
            outTimeQueue.offer(node)
            //runNow(node)
          } else {
            newQueue.offer(node)
          }
        }

        runNow(outTimeQueue)

        startTime += tick
        val nextSleep = startTime - CurrentTimeMillisClock.now
        if (nextSleep > 0) sleep(tick)
        //else System.err.println("timer has no delay")
      }
    }
  }

  companion object {
    val timer by lazy { WheelTimer(200, 1024) }
    val smoothTimer by lazy { WheelTimer(20, 128) }
    fun ScheduledThreadPoolExecutor.scheduleAtFixedRate(
      var2: Long,
      var4: Long,
      var6: TimeUnit,
      var1: () -> Unit,
    ): ScheduledFuture<*> = scheduleAtFixedRate(var1, var2, var4, var6)
  }
}