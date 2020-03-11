package cn.tursom.core

import java.util.concurrent.atomic.AtomicReference

class Disposable<T> {
  private var value = AtomicReference<T>()
  fun set(value: T) {
    this.value.set(value)
  }

  fun get(): T? {
    val value = value.get() ?: return null
    return if (this.value.compareAndSet(value, null)) {
      value
    } else {
      null
    }
  }
}