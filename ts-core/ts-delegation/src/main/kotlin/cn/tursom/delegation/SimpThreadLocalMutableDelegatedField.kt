package cn.tursom.delegation

import cn.tursom.core.SimpThreadLocal


class SimpThreadLocalMutableDelegatedField<in T, V>(
  private val threadLocal: SimpThreadLocal<V>,
  val new: () -> V
) : MutableDelegatedField<T, V> {
  constructor(new: () -> V) : this(SimpThreadLocal(new = new), new)

  override fun setValue(value: V) = threadLocal.set(value)
  override fun getValue(): V = threadLocal.get()
}