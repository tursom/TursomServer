package cn.tursom.core

import java.lang.ref.SoftReference

open class UpdatableThreadLocal<T>(
  @Volatile
  private var new: () -> T,
) : ThreadLocal<T>() {
  @Suppress("LeakingThis")
  private val thisSoft: SoftReference<UpdatableThreadLocal<*>> = SoftReference(this)

  @Volatile
  private var updateTime: Long = System.currentTimeMillis()

  private fun update(): T {
    val value = new()
    set(value)
    updateTimeThreadLocal.get()[thisSoft] = System.currentTimeMillis()
    return value
  }

  override fun get(): T = if (updateTimeThreadLocal.get()[thisSoft] ?: 0 < updateTime) {
    update()
  } else {
    super.get() ?: update()
  }

  fun update(new: () -> T) {
    this.new = new
    updateTime = System.currentTimeMillis()
  }

  companion object {
    private val updateTimeThreadLocal = SimpThreadLocal {
      HashMap<SoftReference<UpdatableThreadLocal<*>>, Long>()
    }
  }
}