package cn.tursom.core.delegation

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.KProperty

class ObjectLockMutableDelegatedField<in T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
  private val lock: Any,
) : MutableDelegatedField<T, V> by delegatedField, DecoratorMutableDelegatedField<T, V> {
  constructor(
    initValue: V,
    lock: Lock = ReentrantLock(),
  ) : this(MutableDelegatedFieldValue(initValue), lock)

  override fun getValue(thisRef: T, property: KProperty<*>): V = synchronized(lock) {
    delegatedField.getValue(thisRef, property)
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) = synchronized(lock) {
    delegatedField.setValue(thisRef, property, value)
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) = synchronized(lock) {
    delegatedField.valueOnSet(thisRef, property, value, oldValue)
  }
}