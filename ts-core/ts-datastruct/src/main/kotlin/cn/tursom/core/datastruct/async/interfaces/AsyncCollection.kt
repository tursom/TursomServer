package cn.tursom.core.datastruct.async.interfaces

interface AsyncCollection<out E> {
  val size: Int

  suspend fun isEmpty(): Boolean
  suspend infix fun contains(element: @UnsafeVariance E): Boolean
  suspend infix fun containsAll(elements: AsyncCollection<@UnsafeVariance E>): Boolean
  suspend fun forEach(action: suspend (E) -> Boolean): Boolean
}