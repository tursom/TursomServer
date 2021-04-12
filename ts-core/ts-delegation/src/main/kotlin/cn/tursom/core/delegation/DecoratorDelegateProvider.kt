package cn.tursom.core.delegation

import kotlin.reflect.KProperty

interface DecoratorDelegateProvider<in T, out V> :
    DelegateProvider<T, DelegatedField<T, V>>,
    DecoratorDelegatedField<T, V> {
    override operator fun provideDelegate(
        thisRef: T,
        prop: KProperty<*>,
    ): DelegatedField<T, V> = delegatedField
}