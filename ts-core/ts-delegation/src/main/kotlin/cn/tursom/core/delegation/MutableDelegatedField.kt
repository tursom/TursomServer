package cn.tursom.core.delegation

import kotlin.reflect.KProperty

interface MutableDelegatedField<in T, V> : DelegatedField<T, V> {
  /**
   * 用来设置值，不会发生 valueOnSet调用
   */
  fun setValue(value: V)

  /**
   * setter委托定义与其默认实现
   */
  operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    valueOnSet(thisRef, property, value, getValue())
    setValue(value)
  }

  /**
   * 当值发生设置时应当被调用
   * 流水线式调用
   */
  fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) {}
}

