package cn.tursom.core.datastruct

interface AbstractMutableList<T> : MutableList<T> {
  override fun contains(element: T): Boolean {
    forEach {
      if (it == element) return true
    }
    return false
  }

  override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
    return SubList(this, fromIndex, toIndex)
  }

  override fun clear() {
    repeat(size) {
      removeAt(size - 1)
    }
  }

  override fun containsAll(elements: Collection<T>): Boolean {
    elements.forEach {
      if (contains(it).not()) {
        return false
      }
    }
    return true
  }

  override fun indexOf(element: T): Int {
    forEachIndexed { i, t ->
      if (element == t) return i
    }
    return -1
  }

  override fun isEmpty(): Boolean = size == 0

  override fun lastIndexOf(element: T): Int {
    var last = -1
    forEachIndexed { index, t ->
      if (t == element) last = index
    }
    return last
  }

  override fun addAll(index: Int, elements: Collection<T>): Boolean {
    elements.forEachIndexed { i, t ->
      add(index + i, t)
    }
    return true
  }

  override fun addAll(elements: Collection<T>): Boolean {
    elements.forEach {
      add(it)
    }
    return true
  }

  override fun retainAll(elements: Collection<T>): Boolean {
    val iterator = iterator()
    var changed = false
    while (iterator.hasNext()) {
      val value = iterator.next()
      if (elements.contains(value).not()) {
        iterator.remove()
        changed = true
      }
    }
    return changed
  }

  override fun listIterator(): MutableListIterator<T> = listIterator(0)

  override fun remove(element: T): Boolean {
    val index = indexOf(element)
    return if (index < 0) {
      false
    } else {
      removeAt(index)
      true
    }
  }

  override fun removeAll(elements: Collection<T>): Boolean {
    var changed = false
    elements.forEach {
      if (remove(it)) {
        changed = true
      }
    }
    return changed
  }

  override fun iterator(): MutableIterator<T> = listIterator()

  override fun listIterator(index: Int): MutableListIterator<T> = AbstractListIterator(this, index)

  class AbstractListIterator<T>(val list: MutableList<T>, var index: Int = 0) : MutableListIterator<T> {
    init {
      index--
    }

    override fun hasPrevious(): Boolean = index > 0

    override fun previousIndex(): Int = index - 1
    override fun previous(): T {
      return list[--index]
    }

    override fun add(element: T) {
      list.add(index, element)
    }

    override fun hasNext(): Boolean = index < list.size
    override fun nextIndex(): Int = index + 1
    override fun next(): T {
      return list[++index]
    }

    override fun remove() {
      list.removeAt(index)
    }

    override fun set(element: T) {
      list[index] = element
    }
  }
}

