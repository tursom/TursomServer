package cn.tursom.delegation

interface DecoratorDelegatedField<in T, out V> {
  val delegatedField: DelegatedField<T, V>
}