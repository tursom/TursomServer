package cn.tursom.core.delegation

import cn.tursom.core.util.uncheckedCast
import kotlin.reflect.KProperty

class FilterDelegatedField<in T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
  private val filter: T.(old: V, new: V) -> Boolean,
) : MutableDelegatedField<T, V> by delegatedField, DecoratorMutableDelegatedField<T, V> {
  companion object Key : DelegatedFieldAttachmentKey<Boolean>

  private var filterResult = false

  override fun <K> get(key: DelegatedFieldAttachmentKey<K>): K? {
    return if (key == Key) filterResult.uncheckedCast() else super.get(key)
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    filterResult = thisRef.filter(getValue(), value)
    if (!filterResult) {
      return
    }
    delegatedField.setValue(thisRef, property, value)
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) {
    filterResult = thisRef.filter(getValue(), value)
    if (!filterResult) {
      return
    }
    delegatedField.valueOnSet(thisRef, property, value, oldValue)
  }
}