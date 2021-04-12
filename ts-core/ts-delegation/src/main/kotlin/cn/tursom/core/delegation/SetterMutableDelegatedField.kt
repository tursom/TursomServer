package cn.tursom.core.delegation

import kotlin.reflect.KProperty

class SetterMutableDelegatedField<T, V>(
  override val mutableDelegatedField: MutableDelegatedField<T, V>,
  val setter: MutableDelegatedField<T, V>.(thisRef: T, property: KProperty<*>, value: V) -> Unit,
) : MutableDelegatedField<T, V> by mutableDelegatedField, DecoratorMutableDelegatedField<T, V> {
  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    mutableDelegatedField.setter(thisRef, property, value)
  }
}
