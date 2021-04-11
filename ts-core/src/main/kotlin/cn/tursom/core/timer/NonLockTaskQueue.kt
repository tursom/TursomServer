package cn.tursom.core.timer

import cn.tursom.core.CurrentTimeMillisClock
import java.util.concurrent.atomic.AtomicReference

class NonLockTaskQueue : TaskQueue {
  private val root = AtomicReference<TaskListNode?>()

  private fun add(taskNode: TaskListNode): TaskListNode {
    while (!root.compareAndSet(taskNode.next, taskNode)) {
      taskNode.next = root.get()
    }
    return taskNode
  }

  override fun offer(task: () -> Unit, timeout: Long): TimerTask {
    return add(TaskListNode(timeout, task, CurrentTimeMillisClock.now, root.get()))
  }

  override fun offer(task: TimerTask): TimerTask {
    if (task.canceled || task.isOutTime) return task
    return if (task is TaskListNode) {
      add(task)
    } else {
      add(TaskListNode(task.timeout, task.task, task.createTime, root.get()))
    }
  }

  override fun take(): TimerTask? {
    var node = root.get()
    while (!root.compareAndSet(node, node?.next)) {
      node = root.get()
    }
    return node
  }

  private class TaskListNode(
    override val timeout: Long,
    override val task: () -> Unit,
    override val createTime: Long,
    @Volatile var next: TaskListNode?
  ) : TimerTask {
    @Volatile
    override var canceled: Boolean = false
    override val outTime: Long = super.outTime

    override fun invoke() {
      task()
    }

    override fun cancel() {
      canceled = true
    }
  }
}