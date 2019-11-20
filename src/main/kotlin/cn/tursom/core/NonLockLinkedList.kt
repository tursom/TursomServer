package cn.tursom.core

import java.util.concurrent.atomic.AtomicReference

open class NonLockLinkedList<T> {
  private val root = AtomicReference<TaskListNode<T>?>()

  infix fun add(element: T) {
    val taskNode = TaskListNode(element, root.get())
    while (!root.compareAndSet(taskNode.next, taskNode)) {
      taskNode.next = root.get()
    }
  }

  infix operator fun invoke(data: T) = add(data)

  fun take(): T? {
    var node = root.get()
    while (!root.compareAndSet(node, node?.next)) {
      node = root.get()
    }
    return node?.data
  }

  private class TaskListNode<T>(
      val data: T,
      @Volatile var next: TaskListNode<T>?
  )

  class NotSupportedException : Exception()
}