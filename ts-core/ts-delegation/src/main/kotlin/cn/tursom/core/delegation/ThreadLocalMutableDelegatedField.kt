package cn.tursom.core.delegation

class ThreadLocalMutableDelegatedField<in T, V>(
  private val threadLocal: ThreadLocal<V?> = ThreadLocal()
) : MutableDelegatedField<T, V?> {
  override fun setValue(value: V?) = threadLocal.set(value)
  override fun getValue(): V? = threadLocal.get()
}