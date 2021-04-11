package cn.tursom.core.datastruct

class MutableSubList<T>(
  val parent: MutableList<T>,
  val fromIndex: Int,
  val toIndex: Int
) : AbstractMutableList<T> {
  override val size: Int get() = toIndex - fromIndex

  override fun add(element: T): Boolean {
    parent.add(toIndex, element)
    return true
  }

  override fun add(index: Int, element: T) = parent.add(fromIndex + index, element)
  override fun removeAt(index: Int): T = parent.removeAt(fromIndex + index)
  override fun retainAll(elements: Collection<T>): Boolean = parent.retainAll(elements)
  override fun set(index: Int, element: T): T = parent.set(fromIndex + index, element)
  override fun get(index: Int): T = parent[fromIndex + index]
}