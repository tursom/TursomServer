package cn.tursom.utils

interface AsyncIterator<T> {
  /**
   * Returns the next element in the iteration.
   */
  suspend operator fun next(): T

  /**
   * Returns `true` if the iteration has more elements.
   */
  suspend operator fun hasNext(): Boolean
}

suspend inline fun <T> AsyncIterator<T>.forEach(action: (T) -> Unit) {
  while (hasNext()) {
    val element = next()
    action(element)
  }
}