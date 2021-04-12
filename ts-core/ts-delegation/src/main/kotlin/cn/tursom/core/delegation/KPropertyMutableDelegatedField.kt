package cn.tursom.core.delegation

import kotlin.reflect.KMutableProperty0

class KPropertyMutableDelegatedField<in T, V>(
    val delegation: KMutableProperty0<V>,
) : MutableDelegatedField<T, V> {
    override fun getValue(): V = delegation.get()
    override fun setValue(value: V) = delegation.set(value)
}