package cn.tursom.core.delegation

import kotlin.reflect.KProperty

class GetterMutableDelegatedField<in T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
  private val getter: MutableDelegatedField<T, V>.(thisRef: T, property: KProperty<*>) -> V,
) : MutableDelegatedField<T, V> by delegatedField, DecoratorMutableDelegatedField<T, V> {
  override fun getValue(thisRef: T, property: KProperty<*>): V = delegatedField.getter(thisRef, property)
}