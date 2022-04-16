package cn.tursom.core.datastruct

class ReversedList<E>(
  val list: List<E>,
) : List<E> by list {
  override fun get(index: Int): E = list[size - index - 1]

  override fun indexOf(element: E): Int {
    val lastIndexOf = list.lastIndexOf(element)
    return if (lastIndexOf >= 0) size - lastIndexOf else -1
  }

  override fun lastIndexOf(element: E): Int {
    val indexOf = list.indexOf(element)
    return if (indexOf >= 0) size - indexOf else -1
  }

  override fun iterator(): Iterator<E> = listIterator()
  override fun listIterator(): ListIterator<E> = listIterator(0)

  override fun listIterator(index: Int): ListIterator<E> = ReverseListIterator(list.listIterator(size - index))

  override fun subList(fromIndex: Int, toIndex: Int): List<E> {
    return ReversedList(list.subList(size - toIndex, size - fromIndex))
  }

  override fun toString(): String {
    val iterator = iterator()
    return buildString {
      append('[')
      iterator.forEach {
        append(it)
        if (iterator.hasNext()) append(", ")
      }
      append(']')
    }
  }

  private class ReverseListIterator<E>(val listIterator: ListIterator<E>) : ListIterator<E> {
    override fun hasNext(): Boolean = listIterator.hasPrevious()
    override fun next(): E = listIterator.previous()
    override fun nextIndex(): Int = listIterator.previousIndex()
    override fun hasPrevious(): Boolean = listIterator.hasNext()
    override fun previous(): E = listIterator.next()
    override fun previousIndex(): Int = listIterator.nextIndex()
  }
}

