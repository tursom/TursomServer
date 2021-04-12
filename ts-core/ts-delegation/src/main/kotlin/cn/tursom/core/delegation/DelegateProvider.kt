package cn.tursom.core.delegation

import kotlin.reflect.KProperty

interface DelegateProvider<in T, out R> {
    operator fun provideDelegate(thisRef: T, prop: KProperty<*>): R
}
