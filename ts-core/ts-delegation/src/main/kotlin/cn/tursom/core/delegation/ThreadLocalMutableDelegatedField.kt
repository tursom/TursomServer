package cn.tursom.core.delegation

import cn.tursom.core.uncheckedCast
import kotlin.reflect.KProperty0

class ThreadLocalMutableDelegatedField<in T, V>(
    private val threadLocal: ThreadLocal<V?> = ThreadLocal(),
) : MutableDelegatedField<T, V?> {

    override fun <K> get(key: DelegatedFieldAttachmentKey<K>): K? {
        return if (key == Companion) {
            threadLocal.uncheckedCast()
        } else {
            super.get(key)
        }
    }

    override fun setValue(value: V?) = threadLocal.set(value)
    override fun getValue(): V? = threadLocal.get()

    companion object : DelegatedFieldAttachmentKey<ThreadLocal<*>> {
        fun <T> getKey() = this.uncheckedCast<DelegatedFieldAttachmentKey<ThreadLocal<T>>>()
        fun <T> getKey(property: KProperty0<T>) = this.uncheckedCast<DelegatedFieldAttachmentKey<ThreadLocal<T>>>()
    }
}