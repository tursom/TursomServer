package cn.tursom.core.datastruct.concurrent

import cn.tursom.core.datastruct.AbstractMutableList
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Suppress("MemberVisibilityCanBePrivate")
class ConcurrentLinkedList<T> : AbstractMutableList<T> {
  private var length = 0
  private var lock = ReentrantReadWriteLock()
  private var root = Node()

  override val size: Int get() = length

  override fun get(index: Int): T {
    val p = prevNodeOfIndex(index).next
    @Suppress("UNCHECKED_CAST")
    return p.value as T
  }

  override fun add(element: T): Boolean = lock.write {
    addBeforeNode(root.prev, element)
    return true
  }

  override fun add(index: Int, element: T) = if (index > size) {
    throw IndexOutOfBoundsException()
  } else lock.write {
    val p = prevNodeOfIndex(index)
    addBeforeNode(p, element)
  }

  fun addAndGetIndex(element: T): Int = lock.write {
    addBeforeNode(root.prev, element)
    return size - 1
  }

  fun addAndGetIterator(element: T): MutableListIterator<T> = lock.write {
    addBeforeNode(root.prev, element)
    return Iterator(size - 1, root.prev)
  }

  override fun clear() = lock.write {
    length = 0
    root.next = root
    root.prev = root
  }

  override fun removeAt(index: Int): T = if (index > size) {
    throw IndexOutOfBoundsException()
  } else lock.write {
    val p = prevNodeOfIndex(index)
    val removed = p.next
    p.next = p.next.next
    p.next.prev = p
    @Suppress("UNCHECKED_CAST")
    removed.value as T
  }

  override fun set(index: Int, element: T): T = if (index < 0 || index > size) {
    throw IndexOutOfBoundsException()
  } else lock.read {
    val p = prevNodeOfIndex(index).next
    val oldValue = p.value
    p.value = element
    @Suppress("UNCHECKED_CAST")
    oldValue as T
  }

  override fun listIterator(index: Int): MutableListIterator<T> = Iterator(index)

  override fun toString(): String {
    return if (length == 0) {
      "[]"
    } else {
      val sb = StringBuilder("[")
      var node = root.next
      while (node != root) {
        sb.append("${node.value}, ")
        node = node.next
      }
      repeat(2) {
        sb.deleteCharAt(sb.length - 1)
      }
      sb.append(']')
      sb.toString()
    }
  }

  private fun addBeforeNode(p: Node, value: T) = lock.write {
    length++
    val new = Node(value, p.next.prev, p.next)
    new.next.prev = new
    new.prev.next = new
  }

  private fun prevNodeOfIndex(index: Int): Node = if (index < 0) {
    root
  } else lock.read {
    var p = root
    if (index <= length / 2) {
      // 从前向后遍历
      repeat(index) {
        p = p.next
      }
    } else {
      // 从后向前遍历
      repeat(length - index + 1) {
        p = p.prev
      }
    }
    return p
  }

  private inner class Node {
    var value: T?
    var prev: Node
    var next: Node

    constructor(value: T? = null, prev: Node, next: Node) {
      this.value = value
      this.next = next
      this.prev = prev
    }

    constructor(value: T? = null) {
      this.value = value
      this.next = this
      this.prev = this
    }
  }

  private inner class Iterator(
    var index: Int,
    var p: Node = prevNodeOfIndex(index - 1)
  ) : MutableListIterator<T> {

    override fun hasPrevious(): Boolean = p.prev != root

    override fun previousIndex(): Int = index - 1
    override fun previous(): T {
      p = p.prev
      index--
      return get()
    }

    override fun add(element: T) {
      addBeforeNode(p, element)
    }

    override fun nextIndex(): Int = index + 1
    override fun hasNext(): Boolean = p.next != root
    override fun next(): T {
      p = p.next
      index++
      return get()
    }

    override fun remove() = lock.write {
      p.next.prev = p.prev
      p.prev.next = p.next
    }

    override fun set(element: T) {
      p.value = element
    }

    fun get(): T {
      @Suppress("UNCHECKED_CAST")
      return p.value as T
    }
  }
}