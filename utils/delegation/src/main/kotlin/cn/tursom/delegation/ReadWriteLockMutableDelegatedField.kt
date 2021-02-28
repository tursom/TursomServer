package cn.tursom.delegation

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlin.concurrent.write
import kotlin.reflect.KProperty

class ReadWriteLockMutableDelegatedField<in T, V>(
  override val mutableDelegatedField: MutableDelegatedField<T, V>,
  private val readWriteLock: ReadWriteLock = ReentrantReadWriteLock(),
) : MutableDelegatedField<T, V> by mutableDelegatedField, DecoratorMutableDelegatedField<T, V> {
  constructor(
    initValue: V,
    readWriteLock: ReadWriteLock = ReentrantReadWriteLock(),
  ) : this(MutableDelegatedFieldValue(initValue), readWriteLock)

  override fun getValue(thisRef: T, property: KProperty<*>): V {
    val rl = readWriteLock.readLock()
    rl.lock()
    try {
      return mutableDelegatedField.getValue(thisRef, property)
    } finally {
      rl.unlock()
    }
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    if (readWriteLock is ReentrantReadWriteLock) readWriteLock.write {
      mutableDelegatedField.setValue(thisRef, property, value)
    } else readWriteLock.writeLock().withLock {
      mutableDelegatedField.setValue(thisRef, property, value)
    }
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) {
    if (readWriteLock is ReentrantReadWriteLock) readWriteLock.write {
      mutableDelegatedField.valueOnSet(thisRef, property, value, oldValue)
    } else readWriteLock.writeLock().withLock {
      mutableDelegatedField.setValue(thisRef, property, value)
    }
  }
}