package cn.tursom.core.delegation

import cn.tursom.core.util.uncheckedCast
import kotlin.reflect.KProperty

class NotNullDelegatedField<in T, out V : Any>(
  override val delegatedField: DelegatedField<T, V?>,
  val ifNull: () -> Nothing = { throw NullPointerException() },
) : DelegatedField<T, V> by delegatedField.uncheckedCast(), DecoratorDelegatedField<T, V?> {
  override fun getValue(): V {
    val value = delegatedField.getValue()
    if (value == null) {
      ifNull()
    } else {
      return value
    }
  }

  override fun getValue(thisRef: T, property: KProperty<*>): V {
    val value = delegatedField.getValue(thisRef, property)
    if (value == null) {
      ifNull()
    } else {
      return value
    }
  }
}