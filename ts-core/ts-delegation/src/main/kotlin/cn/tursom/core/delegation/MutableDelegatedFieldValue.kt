package cn.tursom.core.delegation

import kotlin.reflect.KProperty

open class MutableDelegatedFieldValue<in T, V>(
  private var initValue: V,
) : MutableDelegatedField<T, V> {
  override fun getValue(): V = initValue
  override fun getValue(thisRef: T, property: KProperty<*>): V = initValue
  override fun setValue(value: V) {
    initValue = value
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    initValue = value
  }
}