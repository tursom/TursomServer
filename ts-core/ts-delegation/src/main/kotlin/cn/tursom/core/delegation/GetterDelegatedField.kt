package cn.tursom.core.delegation

import kotlin.reflect.KProperty

class GetterDelegatedField<in T, out V>(
    override val delegatedField: DelegatedField<T, V>,
    private val getter: DelegatedField<T, V>.(thisRef: T, property: KProperty<*>) -> V,
) : DelegatedField<T, V> by delegatedField, DecoratorDelegatedField<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V = delegatedField.getter(thisRef, property)
}