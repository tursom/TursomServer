package cn.tursom.core.delegation

interface Listener<out T, V> {
    fun cancel(): Boolean
    infix fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): Listener<T, V>
    infix fun finally(handler: T.(old: V, new: V) -> Unit): Listener<T, V>
}