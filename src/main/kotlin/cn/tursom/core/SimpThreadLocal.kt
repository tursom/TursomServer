package cn.tursom.core

open class SimpThreadLocal<T>(private val new: () -> T) : ThreadLocal<T>() {
  override fun get(): T = super.get() ?: update()

  private fun update(): T {
    val value = new()
    set(value)
    return value
  }
}

