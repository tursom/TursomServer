package cn.tursom.core.delegation

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KProperty

class LockMutableDelegatedField<in T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
  private val lock: Lock = ReentrantLock(),
) : MutableDelegatedField<T, V> by delegatedField, DecoratorMutableDelegatedField<T, V> {
  constructor(
    initValue: V,
    lock: Lock = ReentrantLock(),
  ) : this(MutableDelegatedFieldValue(initValue), lock)

  override fun getValue(thisRef: T, property: KProperty<*>): V = lock.withLock {
    delegatedField.getValue(thisRef, property)
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) = lock.withLock {
    delegatedField.setValue(thisRef, property, value)
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) = lock.withLock {
    delegatedField.valueOnSet(thisRef, property, value, oldValue)
  }
}