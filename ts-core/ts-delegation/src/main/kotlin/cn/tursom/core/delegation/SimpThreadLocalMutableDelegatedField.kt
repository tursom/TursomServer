package cn.tursom.core.delegation

import cn.tursom.core.SimpThreadLocal
import cn.tursom.core.uncheckedCast
import kotlin.reflect.KProperty0

class SimpThreadLocalMutableDelegatedField<in T, V : Any>(
    private val threadLocal: SimpThreadLocal<V>,
) : MutableDelegatedField<T, V> {
    constructor(new: () -> V) : this(SimpThreadLocal(new = new))


    override fun <K> get(key: DelegatedFieldAttachmentKey<K>): K? {
        return if (key == Companion || key == ThreadLocalMutableDelegatedField) {
            threadLocal.uncheckedCast()
        } else {
            super.get(key)
        }
    }

    override fun setValue(value: V) = threadLocal.set(value)
    override fun getValue(): V = threadLocal.get()

    companion object : DelegatedFieldAttachmentKey<SimpThreadLocal<*>> {
        fun <T : Any> getKey() = this.uncheckedCast<DelegatedFieldAttachmentKey<SimpThreadLocal<T>>>()
        fun <T : Any> getKey(property: KProperty0<T>) =
            this.uncheckedCast<DelegatedFieldAttachmentKey<SimpThreadLocal<T>>>()
    }
}