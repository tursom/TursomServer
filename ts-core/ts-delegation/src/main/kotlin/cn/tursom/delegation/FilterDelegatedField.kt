package cn.tursom.delegation

import cn.tursom.core.cast
import kotlin.reflect.KProperty

class FilterDelegatedField<in T, V>(
  override val mutableDelegatedField: MutableDelegatedField<T, V>,
  private val filter: T.(old: V, new: V) -> Boolean,
) : MutableDelegatedField<T, V> by mutableDelegatedField, DecoratorMutableDelegatedField<T, V> {
  companion object Key : DelegatedFieldAttachmentKey<Boolean>

  private var filterResult = false

  override fun <K> get(key: DelegatedFieldAttachmentKey<K>): K? {
    return if (key == Key) filterResult.cast() else super.get(key)
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    filterResult = thisRef.filter(getValue(), value)
    if (!filterResult) {
      return
    }
    mutableDelegatedField.setValue(thisRef, property, value)
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) {
    filterResult = thisRef.filter(getValue(), value)
    if (!filterResult) {
      return
    }
    mutableDelegatedField.valueOnSet(thisRef, property, value, oldValue)
  }
}