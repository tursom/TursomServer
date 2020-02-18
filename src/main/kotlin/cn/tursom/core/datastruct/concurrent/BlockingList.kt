package cn.tursom.core.datastruct.concurrent

import cn.tursom.core.datastruct.AbstractMutableList
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

open class BlockingList<T>(val list: MutableList<T> = LinkedList()) : AbstractMutableList<T> {
  private var lock = ReentrantReadWriteLock()
  override val size: Int get() = list.size

  override fun add(element: T): Boolean = lock.write {
    list.add(element)
  }

  override fun add(index: Int, element: T) = lock.write {
    list.add(index, element)
  }

  override fun removeAt(index: Int): T = lock.write {
    list.removeAt(index)
  }

  override fun set(index: Int, element: T): T = lock.write {
    list.set(index, element)
  }

  override fun get(index: Int): T = lock.read {
    list[index]
  }
}

