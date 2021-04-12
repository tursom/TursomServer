package cn.tursom.core

open class SimpThreadLocal<T : Any>(
  private val threadLocal: ThreadLocal<T?>? = null,
  val new: () -> T,
) : ThreadLocal<T>() {
  override fun get(): T {
    var value = if (threadLocal != null) threadLocal.get() else super.get()
    return if (value == null) {
      value = new()
      set(value)
      value
    } else {
      value
    }
  }

  override fun set(value: T) {
    if (threadLocal != null) threadLocal.set(value) else super.set(value)
  }

  override fun remove() {
    if (threadLocal != null) threadLocal.remove() else super.remove()
  }
}

