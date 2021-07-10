package cn.tursom.core.delegation.observer

interface ListenableListener<out T, V> : Listener<T, V> {
  infix fun addListener(listener: T.(old: V, new: V) -> Unit): Listener<T, V>
  override fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): ListenableListener<T, V>
}
