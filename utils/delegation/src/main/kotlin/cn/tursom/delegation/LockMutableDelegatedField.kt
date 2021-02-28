package cn.tursom.delegation

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KProperty

class LockMutableDelegatedField<in T, V>(
  override val mutableDelegatedField: MutableDelegatedField<T, V>,
  private val lock: Lock = ReentrantLock(),
) : MutableDelegatedField<T, V> by mutableDelegatedField, DecoratorMutableDelegatedField<T, V> {
  constructor(
    initValue: V,
    lock: Lock = ReentrantLock(),
  ) : this(MutableDelegatedFieldValue(initValue), lock)

  override fun getValue(thisRef: T, property: KProperty<*>): V = lock.withLock {
    mutableDelegatedField.getValue(thisRef, property)
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) = lock.withLock {
    mutableDelegatedField.setValue(thisRef, property, value)
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) = lock.withLock {
    mutableDelegatedField.valueOnSet(thisRef, property, value, oldValue)
  }
}