package cn.tursom.observer

interface Observer<out T, V> {
  fun cancel(): Boolean
  infix fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): Observer<T, V>
  infix fun finally(handler: T.(old: V, new: V) -> Unit): Observer<T, V>
}