package cn.tursom.observer

interface ObservableObserver<out T, V> : Observer<T, V> {
    infix fun addListener(listener: T.(old: V, new: V) -> Unit): Observer<T, V>
    override fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): ObservableObserver<T, V>
}
