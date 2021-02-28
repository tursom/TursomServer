package cn.tursom.core.datastruct

open class AbstractListIterator<E>(
  val list: List<E>,
  private var index: Int = 0
) : ListIterator<E> {
  override fun hasNext(): Boolean = list.size > index
  override fun next(): E = list[index++]
  override fun nextIndex(): Int = index
  override fun hasPrevious(): Boolean = index > 0
  override fun previous(): E = list[--index]

  override fun previousIndex(): Int = index - 1
}