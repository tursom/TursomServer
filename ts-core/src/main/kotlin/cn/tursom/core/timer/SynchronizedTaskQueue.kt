package cn.tursom.core.timer

import cn.tursom.core.CurrentTimeMillisClock

class SynchronizedTaskQueue : TaskQueue {
  val root: TaskNode = TaskNode(0, {}, null, null)

  private fun offer(task: () -> Unit, timeout: Long, createTime: Long): TaskNode {
    synchronized(root) {
      val insert = TaskNode(timeout, task, root, root.next, createTime)
      root.next = insert
      insert.next?.prev = insert
      return insert
    }
  }

  override fun offer(task: () -> Unit, timeout: Long): TaskNode {
    return offer(task, timeout, CurrentTimeMillisClock.now)
  }

  override fun offer(task: TimerTask): TimerTask {
    return if (task is TaskNode) {
      synchronized(root) {
        task.next = root.next
        task.next = task
        task.next?.prev = task
        task
      }
    } else {
      offer(task.task, task.timeout, task.createTime)
    }
  }

  override fun take(): TimerTask? {
    synchronized(root) {
      val node = root.next
      root.next = node?.next
      return node
    }
  }

  inner class TaskNode(
    override val timeout: Long,
    override val task: () -> Unit,
    @Volatile var prev: TaskNode?,
    @Volatile var next: TaskNode?,
    override val createTime: Long = CurrentTimeMillisClock.now,
  ) : TimerTask {
    @Volatile
    override var canceled: Boolean = false
      get() = synchronized(root) {
        field
      }

    override val outTime = createTime + timeout

    override fun invoke() {
      task()
    }

    override fun cancel() {
      synchronized(root) {
        canceled = true
        prev?.next = next
        next?.prev = prev
      }
    }
  }
}