package cn.tursom.core.datastruct

class StepList<E>(
  val list: List<E>,
  val step: Int = 1,
) : List<E> by list {
  init {
    if (step <= 0) throw IllegalArgumentException("step is negative or zero")
  }

  override val size: Int
    get() = when {
      step == 1 -> list.size
      list.isEmpty() -> 0
      else -> list.size / step + if (list.size % step == 0) 0 else 1
    }

  override fun get(index: Int): E = list[index * step]

  override fun indexOf(element: E): Int {
    val indexOf = list.indexOf(element)
    return if (indexOf >= 0) indexOf * step else -1
  }

  override fun lastIndexOf(element: E): Int {
    val indexOf = list.lastIndexOf(element)
    return if (indexOf >= 0) indexOf * step else -1
  }

  override fun iterator(): Iterator<E> = listIterator()
  override fun listIterator(): ListIterator<E> = listIterator(0)
  override fun listIterator(index: Int): ListIterator<E> =
    if (step == 1) list.listIterator(index) else AbstractListIterator(this, index)

  override fun subList(fromIndex: Int, toIndex: Int): List<E> =
    StepList(list.subList(fromIndex * step, toIndex * step), step)

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
}