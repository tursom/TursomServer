package cn.tursom.core.delegation

import kotlin.reflect.KProperty

interface DecoratorMutableDelegateProvider<in T, V> :
  DelegateProvider<T, MutableDelegatedField<T, V>>,
  //DecoratorProvideDelegate<T, V>,
  DecoratorMutableDelegatedField<T, V> {
  override operator fun provideDelegate(thisRef: T, prop: KProperty<*>): MutableDelegatedField<T, V> =
    delegatedField
}
