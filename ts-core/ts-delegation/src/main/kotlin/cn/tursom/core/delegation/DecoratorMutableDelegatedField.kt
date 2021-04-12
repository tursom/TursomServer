package cn.tursom.core.delegation

interface DecoratorMutableDelegatedField<in T, V> : DecoratorDelegatedField<T, V> {
  override val delegatedField: MutableDelegatedField<T, V>
}