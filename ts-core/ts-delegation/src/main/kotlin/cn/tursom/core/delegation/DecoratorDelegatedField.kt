package cn.tursom.core.delegation

interface DecoratorDelegatedField<in T, out V> {
  val delegatedField: DelegatedField<T, V>
}