package cn.tursom.core.delegation

import kotlin.reflect.KProperty

class SetterMutableDelegatedField<T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
  val setter: MutableDelegatedField<T, V>.(thisRef: T, property: KProperty<*>, value: V) -> Unit,
) : MutableDelegatedField<T, V> by delegatedField, DecoratorMutableDelegatedField<T, V> {
  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    delegatedField.setter(thisRef, property, value)
  }
}
