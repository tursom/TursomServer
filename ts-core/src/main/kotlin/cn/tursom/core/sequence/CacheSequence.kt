package cn.tursom.core.sequence

class CacheSequence<T>(
  private val sequence: Sequence<T>,
) : Sequence<T> {
  private var list: MutableList<T>? = null

  override fun iterator(): Iterator<T> = list?.iterator() ?: CacheSequenceIterator(sequence.iterator(), ArrayList())

  inner class CacheSequenceIterator(
    private val iterator: Iterator<T>,
    private val list: MutableList<T>,
  ) : Iterator<T> {
    override fun hasNext(): Boolean = if (iterator.hasNext()) {
      true
    } else {
      this@CacheSequence.list = list
      false
    }

    override fun next(): T {
      val next = iterator.next()
      list.add(next)
      return next
    }
  }
}

fun <T> Sequence<T>.cache() = CacheSequence(this)
