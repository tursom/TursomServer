package cn.tursom.core.timer

import java.lang.Thread.sleep
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.concurrent.thread


class WheelTimer(
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
    thread(isDaemon = true, name = "wheelTimerLooper") {
      val startTime = System.currentTimeMillis()
      while (!closed) {
        position %= wheelSize
        
        val newQueue = TaskQueue()
        val taskQueue = taskQueueArray[position]
        taskQueueArray[position] = newQueue
        
        val time = System.currentTimeMillis()
        var node = taskQueue.root.next
        while (node != null) {
          node = if (node.isOutTime(time)) {
            val sNode = node
            threadPool.execute { sNode.task() }
            node.next
          } else {
            val next = node.next
            newQueue.offer(node)
            next
          }
        }
        
        position++
        val nextSleep = startTime + tick * position - System.currentTimeMillis()
        if (nextSleep > 0) sleep(tick)
      }
    }
  }
  
  
  class TaskQueue {
    val root: TaskNode = TaskNode(0, {}, null, null)
    
    fun offer(task: () -> Unit, timeout: Long): TaskNode {
      synchronized(root) {
        val insert = TaskNode(timeout, task, root, root.next)
        root.next = insert
        insert.next?.prev = insert
        return insert
      }
    }
    
    fun offer(node: TaskNode): TaskNode {
      synchronized(root) {
        node.next = root.next
        node.next = node
        node.next?.prev = node
        return node
      }
    }
    
    inner class TaskNode(
        val timeout: Long,
        val task: () -> Unit,
        var prev: TaskNode?,
        var next: TaskNode?
    ) : TimerTask {
      val outTime = System.currentTimeMillis() + timeout
      val isOutTime get() = System.currentTimeMillis() > outTime
      
      fun isOutTime(time: Long) = time > outTime
      
      override fun run() = task()
      
      override fun cancel() {
        synchronized(root) {
          prev?.next = next
          next?.prev = prev
        }
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
            thread.name = "wheelTimerWorker-$threadNumber"
            return thread
          }
        })
    val timer by lazy { WheelTimer(200, 1024) }
    val smoothTimer by lazy { WheelTimer(20, 128) }
  }
}