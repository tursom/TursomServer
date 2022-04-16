package cn.tursom.core

import java.util.concurrent.atomic.AtomicReference

class Disposable<T>(
  value: T? = null,
) {
  private var value = AtomicReference<T>()

  init {
    if (value != null) {
      this.value.set(value)
    }
  }

  fun set(value: T) {
    this.value.set(value)
  }

  fun get(): T? = value.getAndSet(null)
}