package cn.tursom.delegation

import kotlin.reflect.KProperty0

class KPropertyDelegatedField<in T, out V>(
  val delegation: KProperty0<V>,
) : DelegatedField<T, V> {
  override fun getValue(): V = delegation.get()
}