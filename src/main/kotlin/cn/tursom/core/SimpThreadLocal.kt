package cn.tursom.core

open class SimpThreadLocal<T>(val new: () -> T) : ThreadLocal<T>() {
  override fun get(): T {
    var value = super.get()
    if (value == null) {
      value = new()
      set(value)
    }
    return value
  }
}