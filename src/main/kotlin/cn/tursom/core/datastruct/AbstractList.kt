package cn.tursom.core.datastruct

interface AbstractList<T> : List<T> {

  override fun contains(element: T): Boolean {
    forEach {
      if (it == element) {
        return true
      }
    }
    return false
  }

  override fun containsAll(elements: Collection<T>): Boolean {
    elements.forEach {
      if (!contains(it)) return false
    }
    return true
  }

  override fun get(index: Int): T {
    forEachIndexed { i, t ->
      if (index == i) {
        return t
      }
    }
    throw IndexOutOfBoundsException()
  }

  override fun indexOf(element: T): Int {
    forEachIndexed { i, t ->
      if (t == element) {
        return i
      }
    }
    return -1
  }

  override fun isEmpty(): Boolean {
    return size == 0
  }

  override fun iterator(): Iterator<T> = listIterator()

  override fun lastIndexOf(element: T): Int {
    var lastIndex = -1
    forEachIndexed { i, t ->
      if (t == element) {
        lastIndex = i
      }
    }
    return lastIndex
  }

  override fun listIterator(): ListIterator<T> = listIterator(0)

  override fun subList(fromIndex: Int, toIndex: Int): List<T> = SubList(this, fromIndex, toIndex)
}

class SubList<T>(val list: List<T>, val fromIndex: Int, val toIndex: Int) : AbstractList<T> {
  override val size: Int = toIndex - fromIndex

  override fun listIterator(index: Int): ListIterator<T> = object : ListIterator<T> {
    var i: Int = index - 1

    override fun hasPrevious(): Boolean = i > 0
    override fun previousIndex(): Int = i - 1
    override fun previous(): T = this@SubList[--i]
    override fun hasNext(): Boolean = i < this@SubList.size
    override fun nextIndex(): Int = i + 1
    override fun next(): T = this@SubList[++i]

  }
}