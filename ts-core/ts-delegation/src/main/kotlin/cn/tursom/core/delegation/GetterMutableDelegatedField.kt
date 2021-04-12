package cn.tursom.core.delegation

import kotlin.reflect.KProperty

class GetterMutableDelegatedField<in T, V>(
  override val mutableDelegatedField: MutableDelegatedField<T, V>,
  private val getter: MutableDelegatedField<T, V>.(thisRef: T, property: KProperty<*>) -> V,
) : MutableDelegatedField<T, V> by mutableDelegatedField, DecoratorMutableDelegatedField<T, V> {
  override fun getValue(thisRef: T, property: KProperty<*>): V = mutableDelegatedField.getter(thisRef, property)
}